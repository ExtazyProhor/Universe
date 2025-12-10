package ru.prohor.universe.scarif.services;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.scarif.data.refresh.JpaRefreshTokenMethods;
import ru.prohor.universe.scarif.data.refresh.RefreshToken;
import ru.prohor.universe.scarif.data.session.JpaSessionMethods;
import ru.prohor.universe.scarif.data.session.Session;
import ru.prohor.universe.scarif.services.refresh.ParsedUserToken;
import ru.prohor.universe.scarif.services.refresh.RefreshTokenService;
import ru.prohor.universe.scarif.services.refresh.Tokens;
import ru.prohor.universe.scarif.services.refresh.UserTokenAndUserId;

import java.util.List;

@Transactional
@Service
public class SessionsService {
    private static final Logger log = LoggerFactory.getLogger(SessionsService.class);

    private final Duration refreshTokenTtl;
    private final JpaRefreshTokenMethods refreshTokenMethods;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final RefreshTokenService refreshTokenService;
    private final JpaSessionMethods sessionMethods;

    public SessionsService(
            @Value("${universe.scarif.refreshTokenTtlDays}") long refreshTokenTtlDays,
            JpaRefreshTokenMethods refreshTokenMethods,
            SnowflakeIdGenerator snowflakeIdGenerator,
            RefreshTokenService refreshTokenService,
            JpaSessionMethods sessionMethods
    ) {
        this.refreshTokenTtl = Duration.standardDays(refreshTokenTtlDays);
        this.refreshTokenMethods = refreshTokenMethods;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
        this.refreshTokenService = refreshTokenService;
        this.sessionMethods = sessionMethods;
    }

    public List<Session> getAllValidSessions(long userId) {
        return sessionMethods.findByUserIdAndClosedFalseAndExpiresAtAfter(userId, DateTimeUtil.unwrap(Instant.now()))
                .stream()
                .map(Session::fromDto)
                .toList();
    }

    // TODO Лимит одновременных сессий
    public String createNewSession(long userId, String userAgent, String ipAddress) {
        Instant now = Instant.now();
        Session session = new Session(
                snowflakeIdGenerator.nextId(),
                userId,
                now,
                now.plus(refreshTokenTtl),
                Opt.ofNullable(userAgent),
                ipAddress,
                false,
                Opt.empty()
        );
        sessionMethods.save(session.toDto());

        Tokens tokens = refreshTokenService.generateTokens();
        createNewRefreshToken(
                tokens,
                session,
                Instant.now()
        );
        return tokens.userToken();
    }

    public Opt<UserTokenAndUserId> refreshToken(ParsedUserToken userToken) {
        Opt<RefreshToken> refreshToken = validateParsedUserToken(userToken);
        if (refreshToken.isEmpty())
            return Opt.empty();
        refreshTokenMethods.save(revoke(refreshToken.get()).toDto());

        Tokens tokens = refreshTokenService.generateTokens();
        createNewRefreshToken(
                tokens,
                refreshToken.get().session(),
                Instant.now()
        );
        return Opt.of(new UserTokenAndUserId(
                refreshToken.get().session().userId(),
                tokens.userToken()
        ));
    }

    private void createNewRefreshToken(Tokens tokens, Session session, Instant createAt) {
        RefreshToken token = new RefreshToken(tokens.id(), tokens.token(), session, createAt, false, Opt.empty());
        refreshTokenMethods.save(token.toDto());
    }

    public Opt<Session> getSession(long id) {
        return Opt.wrap(sessionMethods.findById(id)).map(Session::fromDto);
    }

    public void closeSession(Session session) {
        sessionMethods.save(close(session).toDto());
    }

    public boolean logout(ParsedUserToken parsedUserToken) {
        Opt<Session> session = validateParsedUserToken(parsedUserToken).map(RefreshToken::session);
        if (session.isEmpty())
            return false;
        sessionMethods.save(session.map(this::close).get().toDto());
        return true;
    }

    public Opt<Session> getSession(ParsedUserToken userToken) {
        return validateParsedUserToken(userToken).map(RefreshToken::session);
    }

    private Opt<RefreshToken> validateParsedUserToken(ParsedUserToken userToken) {
        Opt<RefreshToken> refreshTokenO = findRefreshToken(userToken.id());
        if (refreshTokenO.isEmpty()) {
            log.warn("[IS] refresh token cookie contains id of a non-existent refresh token");
            return Opt.empty();
        }
        RefreshToken refreshToken = refreshTokenO.get();
        if (!userToken.tokenValidator().test(refreshToken)) {
            log.warn("[IS] fake validation token");
            return Opt.empty();
        }
        if (refreshToken.revoked()) {
            log.warn(
                    "[IS] user with id {} is trying to use a revoked token with id {}",
                    refreshToken.session().userId(),
                    refreshToken.id()
            );
            return Opt.empty();
        }
        Session session = refreshToken.session();
        if (session.closed()) {
            log.debug("user-token refers to a closed session");
            return Opt.empty();
        }
        if (session.expiresAt().isBeforeNow()) {
            log.debug("session {} of user with id {} has expired", session.id(), session.userId());
            return Opt.empty();
        }
        return refreshTokenO;
    }

    private Opt<RefreshToken> findRefreshToken(long id) {
        return Opt.wrap(refreshTokenMethods.findById(id)).map(RefreshToken::fromDto);
    }

    private Session close(Session session) {
        return new Session(
                session.id(),
                session.userId(),
                session.createdAt(),
                session.expiresAt(),
                session.userAgent(),
                session.ipAddress(),
                true,
                Opt.of(Instant.now())
        );
    }

    private RefreshToken revoke(RefreshToken token) {
        return new RefreshToken(
                token.id(),
                token.token(),
                token.session(),
                token.createdAt(),
                true,
                Opt.of(Instant.now())
        );
    }
}
