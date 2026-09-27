package ru.prohor.universe.scarif.services.session;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.springweb.CookieUtil;
import ru.prohor.universe.scarif.data.RotatedRefreshToken;
import ru.prohor.universe.scarif.data.Session;
import ru.prohor.universe.scarif.data.User;
import ru.prohor.universe.scarif.jwtprovider.AccessJwtProvider;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.refresh.RefreshJwtProvider;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;
import ru.prohor.universe.scarif.web.UserData;
import ru.prohor.universe.scarif.web.api.AccessTokenResponse;
import ru.prohor.universe.scarif.web.api.SessionDescription;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SessionsService {
    private static final Logger log = LoggerFactory.getLogger(SessionsService.class);

    private final Duration refreshTokenTtl;
    private final Duration refreshTokenRotateWindow;
    private final CookieProvider cookieProvider;
    private final RefreshJwtProvider refreshJwtProvider;
    private final AccessJwtProvider accessJwtProvider;
    private final MongoRepository<User> usersRepository;
    private final ResponseEntity<?> unauthorizedResponse;

    public SessionsService(
            @Value("${universe.scarif.refresh-token.ttl}") Duration refreshTokenTtl,
            @Value("${universe.scarif.refresh-token.rotate-window}") Duration refreshTokenRotateWindow,
            CookieProvider cookieProvider,
            RefreshJwtProvider refreshJwtProvider,
            AccessJwtProvider accessJwtProvider,
            MongoRepository<User> usersRepository
    ) {
        this.refreshTokenTtl = refreshTokenTtl;
        this.refreshTokenRotateWindow = refreshTokenRotateWindow;
        this.cookieProvider = cookieProvider;
        this.refreshJwtProvider = refreshJwtProvider;
        this.accessJwtProvider = accessJwtProvider;
        this.usersRepository = usersRepository;
        this.unauthorizedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .headers(CookieUtil.setCookieHeader(cookieProvider.clearRefreshCookie()))
                .build();
    }

    public SessionData createNewSession(User user, UserData userData) {
        ObjectId refreshTokenId = ObjectId.get();

        Instant now = Instant.now();
        Session session = new Session(
                ObjectId.get(),
                now,
                now.plus(refreshTokenTtl),
                userData.userAgent(),
                userData.ip(),
                false,
                Opt.empty(),
                List.of(),
                Opt.of(refreshTokenId)
        );
        String refreshToken = getRefreshToken(session, user.id(), refreshTokenId);
        String accessToken = accessJwtProvider.getToken(user.numericId(), user.uuid(), user.id(), session.id());
        return new SessionData(session, refreshToken, accessToken);
    }

    public ResponseEntity<AccessTokenResponse> newSessionResponse(
            String accessJwt,
            String refreshJwt
    ) {
        return ResponseEntity.ok()
                .headers(CookieUtil.setCookieHeader(cookieProvider.createRefreshCookie(refreshJwt)))
                .body(new AccessTokenResponse(accessJwt));
    }

    public ResponseEntity<?> refresh(RefreshToken refreshToken) {
        return usersRepository.withTransaction(tx -> {
            User user = tx.ensuredFindById(refreshToken.userId());
            Session session = findSession(user, refreshToken.sessionId());
            if (session.closed()) {
                log.info("failed to refresh: session was closed");
                return unauthorizedResponse;
            }

            Instant now = Instant.now();
            if (session.expiresAt().isBefore(now)) {
                // технически это должно быть почти невозможно, так как у сессии и токена в jwt равный expires
                log.warn(
                        "failed to refresh: session was expired {} millis ago",
                        Duration.between(session.expiresAt(), now).toMillis()
                );
                return unauthorizedResponse;
            }

            ObjectId currentRefreshTokenId = session.refreshToken().get();
            if (!refreshToken.refreshTokenId().equals(currentRefreshTokenId)) {
                Optional<RotatedRefreshToken> rotated = session.recentlyRotatedTokens().stream()
                        .filter(token -> token.id().equals(refreshToken.refreshTokenId()))
                        .findAny();

                if (rotated.isPresent()) {
                    if (rotated.get().rotatedAt().plus(refreshTokenRotateWindow).isAfter(now)) {
                        return newSessionResponse(
                                accessJwtProvider.getToken(user.numericId(), user.uuid(), user.id(), session.id()),
                                getRefreshToken(session, user.id(), currentRefreshTokenId)
                        );
                    }
                }

                log.warn(
                        "request with old token (expired more than {} seconds ago) that should have been deleted",
                        refreshTokenRotateWindow.toSeconds()
                );
                return unauthorizedResponse;
            }

            ObjectId newRefreshTokenId = ObjectId.get();
            String newRefreshToken = getRefreshToken(session, user.id(), newRefreshTokenId);
            String accessToken = accessJwtProvider.getToken(user.numericId(), user.uuid(), user.id(), session.id());
            List<RotatedRefreshToken> rotated = new ArrayList<>(session.recentlyRotatedTokens());
            rotated.add(new RotatedRefreshToken(refreshToken.refreshTokenId(), now));

            session = session.toBuilder()
                    .refreshToken(Opt.of(newRefreshTokenId))
                    .recentlyRotatedTokens(rotated)
                    .build();
            user = appendSession(user, session, now);
            tx.save(user);

            return newSessionResponse(accessToken, newRefreshToken);
        });
    }

    public ResponseEntity<?> closeSession(RefreshToken refreshToken, String sessionId) {
        ObjectId sessionIdToClose;
        try {
            sessionIdToClose = new ObjectId(sessionId);
        } catch (IllegalArgumentException e) {
            log.warn("illegal format of sessionsId: {}", sessionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return usersRepository.withTransaction(tx -> {
            User user = tx.ensuredFindById(refreshToken.userId());
            Optional<Session> session = user.sessions().stream()
                    .filter(it -> it.id().equals(sessionIdToClose))
                    .findAny();
            if (session.isEmpty()) {
                log.warn("user has no session to close with sessionId {}", sessionIdToClose);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            if (session.get().id().equals(refreshToken.sessionId())) {
                log.warn("sessionId of the current session is specified for session removal");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Session updated = session.get().toBuilder().closed(true).build();
            log.info("session with id {} was closed", sessionIdToClose);
            tx.save(appendSession(user, updated, Instant.now()));
            return ResponseEntity.ok().build();
        });
    }

    public ResponseEntity<?> logout(RefreshToken refreshToken) {
        usersRepository.safeUpdate(
                refreshToken.userId(), user -> {
                    Session session = findSession(user, refreshToken.sessionId()).toBuilder()
                            .closed(true)
                            .build();
                    return appendSession(user, session, Instant.now());
                }
        );
        return ResponseEntity.ok().headers(CookieUtil.setCookieHeader(cookieProvider.clearRefreshCookie())).build();
    }

    public ResponseEntity<List<SessionDescription>> getSessions(RefreshToken refreshToken) {
        Instant now = Instant.now();
        User user = usersRepository.ensuredFindById(refreshToken.userId());
        List<SessionDescription> response = user.sessions().stream()
                .filter(session -> !session.closed() && session.expiresAt().isAfter(now))
                .sorted(Comparator.comparing(Session::createdAt).thenComparing(Session::id))
                .map(session -> new SessionDescription(
                        session.ipAddress(),
                        session.userAgent(),
                        session.id(),
                        DateTimeUtil.toReadableString(session.createdAt()),
                        session.id().equals(refreshToken.sessionId())
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    private User appendSession(User user, Session session, Instant now) {
        if (!session.recentlyRotatedTokens().isEmpty()) {
            Instant lastPossibleRotatedInstant = now.minus(refreshTokenRotateWindow);
            List<RotatedRefreshToken> rotated = session.recentlyRotatedTokens().stream()
                    .filter(it -> it.rotatedAt().isAfter(lastPossibleRotatedInstant))
                    .toList();
            session = session.toBuilder().recentlyRotatedTokens(rotated).build();
        }
        Session sessionFinal = session;

        List<Session> sessions = user.sessions().stream()
                .filter(s -> !s.id().equals(sessionFinal.id()))
                .collect(Collectors.toCollection(ArrayList::new));
        sessions.add(sessionFinal);
        return user.toBuilder().sessions(sessions).build();
    }

    private Session findSession(User user, ObjectId sessionId) {
        List<Session> sessions = user.sessions().stream()
                .filter(session -> session.id().equals(sessionId))
                .toList();
        if (sessions.size() != 1) {
            throw new IllegalStateException(
                    "illegal sessions count with id {" + sessionId + "}, sessions count = " + sessions.size()
            );
        }
        return sessions.getFirst();
    }

    private String getRefreshToken(Session session, ObjectId userId, ObjectId refreshTokenId) {
        return refreshJwtProvider.getToken(userId, session.id(), refreshTokenId, session.expiresAt());
    }
}
