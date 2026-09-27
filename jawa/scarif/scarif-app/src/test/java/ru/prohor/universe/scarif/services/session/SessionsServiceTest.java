package ru.prohor.universe.scarif.services.session;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
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
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.prohor.universe.scarif.services.session.SessionsServiceTestSupport.session;
import static ru.prohor.universe.scarif.services.session.SessionsServiceTestSupport.user;

class SessionsServiceTest {
    private static final Duration REFRESH_TTL = Duration.ofDays(30);
    private static final Duration ROTATE_WINDOW = Duration.ofSeconds(10);

    private final CookieProvider cookieProvider = mock(CookieProvider.class);
    private final RefreshJwtProvider refreshJwtProvider = mock(RefreshJwtProvider.class);
    private final AccessJwtProvider accessJwtProvider = mock(AccessJwtProvider.class);
    private final MongoRepository<User> usersRepository = mockMongoRepository();

    private SessionsService service;

    @SuppressWarnings("unchecked")
    private MongoRepository<User> mockMongoRepository() {
        return mock(MongoRepository.class);
    }

    @BeforeEach
    void setUp() {
        when(cookieProvider.clearRefreshCookie()).thenReturn("cleared-cookie");
        when(cookieProvider.createRefreshCookie(anyString())).thenReturn("created-cookie");
        doAnswer(invocation -> {
            MonoFunction<MongoRepository<User>, ?> transaction = invocation.getArgument(0);
            return transaction.apply(usersRepository);
        }).when(usersRepository).withTransaction(
                ArgumentMatchers.<MonoFunction<MongoRepository<User>, Object>>any()
        );
        service = new SessionsService(
                REFRESH_TTL, ROTATE_WINDOW, cookieProvider, refreshJwtProvider, accessJwtProvider, usersRepository
        );
        clearInvocations(cookieProvider);
    }

    @Test
    void createNewSessionBuildsSessionAndTokens() {
        User newUser = user(ObjectId.get(), List.of());
        UserData userData = new UserData("1.2.3.4", Opt.of("agent"));
        when(refreshJwtProvider.getToken(any(), any(), any(), any())).thenReturn("refresh-jwt");
        when(accessJwtProvider.getToken(anyLong(), any(), any(), any())).thenReturn("access-jwt");

        SessionData data = service.createNewSession(newUser, userData);

        assertEquals("refresh-jwt", data.refreshJwt());
        assertEquals("access-jwt", data.accessJwt());
        assertEquals("1.2.3.4", data.session().ipAddress());
        assertEquals(Opt.of("agent"), data.session().userAgent());
        assertFalse(data.session().closed());
        assertTrue(data.session().recentlyRotatedTokens().isEmpty());
        assertTrue(data.session().refreshToken().isPresent());

        ArgumentCaptor<Instant> expiresCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(refreshJwtProvider).getToken(eq(newUser.id()), eq(data.session().id()), any(), expiresCaptor.capture());
        assertEquals(data.session().expiresAt(), expiresCaptor.getValue());
    }

    @Test
    void newSessionResponseSetsCookieAndBody() {
        ResponseEntity<AccessTokenResponse> response = service.newSessionResponse("access", "refresh");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("access", response.getBody().accessToken());
        verify(cookieProvider).createRefreshCookie("refresh");
        assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE));
    }

    @Test
    void refreshWithCurrentTokenRotatesAndSaves() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        Session session = session(
                sessionId,
                Instant.now().plusSeconds(600),
                false,
                Opt.of(currentRefreshTokenId),
                List.of()
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);
        when(refreshJwtProvider.getToken(any(), any(), any(), any())).thenReturn("new-refresh-jwt");
        when(accessJwtProvider.getToken(anyLong(), any(), any(), any())).thenReturn("new-access-jwt");

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, currentRefreshTokenId);
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(savedCaptor.capture());
        Session savedSession = savedCaptor.getValue().sessions().stream()
                .filter(s -> s.id().equals(sessionId)).findFirst().orElseThrow();
        assertTrue(savedSession.refreshToken().isPresent());
        assertNotEquals(currentRefreshTokenId, savedSession.refreshToken().get());
        assertEquals(1, savedSession.recentlyRotatedTokens().size());
        assertEquals(currentRefreshTokenId, savedSession.recentlyRotatedTokens().getFirst().id());
    }

    @Test
    void refreshPrunesStaleRotatedTokensOnSuccessfulRotation() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        ObjectId staleTokenId = ObjectId.get();
        RotatedRefreshToken stale = new RotatedRefreshToken(
                staleTokenId,
                Instant.now().minus(ROTATE_WINDOW).minusSeconds(5)
        );
        Session session = session(
                sessionId, Instant.now().plusSeconds(600), false,
                Opt.of(currentRefreshTokenId), List.of(stale)
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);
        when(refreshJwtProvider.getToken(any(), any(), any(), any())).thenReturn("new-refresh-jwt");
        when(accessJwtProvider.getToken(anyLong(), any(), any(), any())).thenReturn("new-access-jwt");

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, currentRefreshTokenId);
        service.refresh(refreshToken);

        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(savedCaptor.capture());
        Session saved = savedCaptor.getValue().sessions().getFirst();
        assertEquals(1, saved.recentlyRotatedTokens().size(), "устаревший rotated-токен должен быть вычищен");
        assertEquals(currentRefreshTokenId, saved.recentlyRotatedTokens().getFirst().id());
    }

    @Test
    void refreshReturnsUnauthorizedWhenSessionClosed() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        Session session = session(
                sessionId,
                Instant.now().plusSeconds(600),
                true,
                Opt.of(currentRefreshTokenId),
                List.of()
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, currentRefreshTokenId);
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void refreshReturnsUnauthorizedWhenSessionExpired() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        Session session = session(
                sessionId,
                Instant.now().minusSeconds(1),
                false,
                Opt.of(currentRefreshTokenId),
                List.of()
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, currentRefreshTokenId);
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void refreshWithRecentlyRotatedTokenWithinWindowReissuesAccessTokenWithoutRotating() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        ObjectId oldRotatedTokenId = ObjectId.get();
        RotatedRefreshToken rotated = new RotatedRefreshToken(oldRotatedTokenId, Instant.now().minusSeconds(2));
        Session session = session(
                sessionId, Instant.now().plusSeconds(600), false,
                Opt.of(currentRefreshTokenId), List.of(rotated)
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);
        when(refreshJwtProvider.getToken(any(), any(), any(), any())).thenReturn("reissued-refresh-jwt");
        when(accessJwtProvider.getToken(anyLong(), any(), any(), any())).thenReturn("new-access-jwt");

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, oldRotatedTokenId);
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));

        ArgumentCaptor<ObjectId> refreshTokenIdCaptor = ArgumentCaptor.forClass(ObjectId.class);
        verify(refreshJwtProvider).getToken(eq(userId), eq(sessionId), refreshTokenIdCaptor.capture(), any());
        assertEquals(
                currentRefreshTokenId, refreshTokenIdCaptor.getValue(),
                "должен переиздать ТЕКУЩИЙ refresh-токен, а не создавать новый"
        );
    }

    @Test
    void refreshWithRotatedTokenOutsideWindowIsUnauthorized() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        ObjectId oldRotatedTokenId = ObjectId.get();
        RotatedRefreshToken rotated = new RotatedRefreshToken(
                oldRotatedTokenId, Instant.now().minus(ROTATE_WINDOW).minusSeconds(5)
        );
        Session session = session(
                sessionId, Instant.now().plusSeconds(600), false,
                Opt.of(currentRefreshTokenId), List.of(rotated)
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, oldRotatedTokenId);
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void refreshWithUnknownTokenIsUnauthorized() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        ObjectId currentRefreshTokenId = ObjectId.get();
        Session session = session(
                sessionId,
                Instant.now().plusSeconds(600),
                false,
                Opt.of(currentRefreshTokenId),
                List.of()
        );
        User user = user(userId, List.of(session));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, ObjectId.get());
        ResponseEntity<?> response = service.refresh(refreshToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void closeSessionRejectsInvalidObjectIdFormat() {
        RefreshToken refreshToken = new RefreshToken(ObjectId.get(), ObjectId.get(), ObjectId.get());

        ResponseEntity<?> response = service.closeSession(refreshToken, "not-an-object-id");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(usersRepository);
    }

    @Test
    void closeSessionRejectsUnknownSessionId() {
        ObjectId userId = ObjectId.get();
        ObjectId currentSessionId = ObjectId.get();
        User user = user(
                userId, List.of(
                        session(
                                currentSessionId,
                                Instant.now().plusSeconds(600),
                                false,
                                Opt.of(ObjectId.get()),
                                List.of()
                        )
                )
        );
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, currentSessionId, ObjectId.get());
        ResponseEntity<?> response = service.closeSession(refreshToken, ObjectId.get().toHexString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void closeSessionRejectsClosingCurrentSession() {
        ObjectId userId = ObjectId.get();
        ObjectId currentSessionId = ObjectId.get();
        User user = user(
                userId, List.of(
                        session(
                                currentSessionId,
                                Instant.now().plusSeconds(600),
                                false,
                                Opt.of(ObjectId.get()),
                                List.of()
                        )
                )
        );
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, currentSessionId, ObjectId.get());
        ResponseEntity<?> response = service.closeSession(refreshToken, currentSessionId.toHexString());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(usersRepository, never()).save(any(User.class));
    }

    @Test
    void closeSessionClosesOtherSession() {
        ObjectId userId = ObjectId.get();
        ObjectId currentSessionId = ObjectId.get();
        ObjectId otherSessionId = ObjectId.get();
        Session current = session(
                currentSessionId,
                Instant.now().plusSeconds(600),
                false,
                Opt.of(ObjectId.get()),
                List.of()
        );
        Session other = session(
                otherSessionId,
                Instant.now().plusSeconds(600),
                false,
                Opt.of(ObjectId.get()),
                List.of()
        );
        User user = user(userId, List.of(current, other));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, currentSessionId, ObjectId.get());
        ResponseEntity<?> response = service.closeSession(refreshToken, otherSessionId.toHexString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(savedCaptor.capture());
        Session closedSession = savedCaptor.getValue().sessions().stream()
                .filter(s -> s.id().equals(otherSessionId)).findFirst().orElseThrow();
        assertTrue(closedSession.closed());
        Session untouchedCurrent = savedCaptor.getValue().sessions().stream()
                .filter(s -> s.id().equals(currentSessionId)).findFirst().orElseThrow();
        assertFalse(untouchedCurrent.closed());
    }

    @Test
    @SuppressWarnings("unchecked")
    void logoutClosesSessionAndClearsCookie() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        User user = user(
                userId, List.of(
                        session(sessionId, Instant.now().plusSeconds(600), false, Opt.of(ObjectId.get()), List.of())
                )
        );

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, ObjectId.get());
        ResponseEntity<?> response = service.logout(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cookieProvider).clearRefreshCookie();
        assertNotNull(response.getHeaders().get(HttpHeaders.SET_COOKIE));

        ArgumentCaptor<Function<User, User>> updaterCaptor = ArgumentCaptor.forClass(Function.class);
        verify(usersRepository).safeUpdate(eq(userId), (MonoFunction<User, User>) updaterCaptor.capture());
        User updated = updaterCaptor.getValue().apply(user);
        Session updatedSession = updated.sessions().stream()
                .filter(s -> s.id().equals(sessionId)).findFirst().orElseThrow();
        assertTrue(updatedSession.closed());
    }

    @Test
    @SuppressWarnings("unchecked")
    void logoutUpdaterThrowsWhenSessionNotFound() {
        ObjectId userId = ObjectId.get();
        ObjectId sessionId = ObjectId.get();
        User userWithoutSession = user(userId, List.of());

        RefreshToken refreshToken = new RefreshToken(userId, sessionId, ObjectId.get());
        service.logout(refreshToken);

        ArgumentCaptor<Function<User, User>> updaterCaptor = ArgumentCaptor.forClass(Function.class);
        verify(usersRepository).safeUpdate(eq(userId), (MonoFunction<User, User>) updaterCaptor.capture());
        assertThrows(IllegalStateException.class, () -> updaterCaptor.getValue().apply(userWithoutSession));
    }

    @Test
    void getSessionsFiltersClosedAndExpiredAndMarksCurrent() {
        ObjectId userId = ObjectId.get();
        ObjectId currentSessionId = ObjectId.get();
        Instant now = Instant.now();

        Session current = session(currentSessionId, now.plusSeconds(600), false, Opt.of(ObjectId.get()), List.of());
        Session other = session(ObjectId.get(), now.plusSeconds(600), false, Opt.of(ObjectId.get()), List.of());
        Session closed = session(ObjectId.get(), now.plusSeconds(600), true, Opt.of(ObjectId.get()), List.of());
        Session expired = session(ObjectId.get(), now.minusSeconds(1), false, Opt.of(ObjectId.get()), List.of());

        User user = user(userId, List.of(current, other, closed, expired));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, currentSessionId, ObjectId.get());
        ResponseEntity<List<SessionDescription>> response = service.getSessions(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SessionDescription> descriptions = response.getBody();
        assertNotNull(descriptions);
        assertEquals(2, descriptions.size());
        assertTrue(descriptions.stream().noneMatch(d -> d.id().equals(closed.id()) || d.id().equals(expired.id())));

        SessionDescription currentDescription = descriptions.stream()
                .filter(d -> d.id().equals(currentSessionId)).findFirst().orElseThrow();
        assertTrue(currentDescription.current());
        assertEquals(current.ipAddress(), currentDescription.ip());
        assertEquals(current.userAgent(), currentDescription.userAgent());
        assertEquals(DateTimeUtil.toReadableString(current.createdAt()), currentDescription.createdAt());

        SessionDescription otherDescription = descriptions.stream()
                .filter(d -> d.id().equals(other.id())).findFirst().orElseThrow();
        assertFalse(otherDescription.current());
    }

    @Test
    void getSessionsSortsByCreatedAtThenId() {
        ObjectId userId = ObjectId.get();
        Instant base = Instant.now();

        Session later = new Session(
                ObjectId.get(), base.plusSeconds(10), base.plusSeconds(600),
                Opt.empty(), "1.1.1.1", false, Opt.empty(), List.of(), Opt.of(ObjectId.get())
        );
        Session earlier = new Session(
                ObjectId.get(), base, base.plusSeconds(600),
                Opt.empty(), "2.2.2.2", false, Opt.empty(), List.of(), Opt.of(ObjectId.get())
        );

        User user = user(userId, List.of(later, earlier));
        when(usersRepository.ensuredFindById(userId)).thenReturn(user);

        RefreshToken refreshToken = new RefreshToken(userId, ObjectId.get(), ObjectId.get());
        List<SessionDescription> descriptions = service.getSessions(refreshToken).getBody();

        assertNotNull(descriptions);
        assertEquals(earlier.id(), descriptions.get(0).id());
        assertEquals(later.id(), descriptions.get(1).id());
    }
}
