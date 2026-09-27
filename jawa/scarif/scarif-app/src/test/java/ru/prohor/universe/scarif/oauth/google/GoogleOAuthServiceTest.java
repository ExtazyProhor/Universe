package ru.prohor.universe.scarif.oauth.google;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Claim;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.functional.MonoFunction;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.scarif.data.ExternalAccount;
import ru.prohor.universe.scarif.data.ExternalAccountProvider;
import ru.prohor.universe.scarif.data.Session;
import ru.prohor.universe.scarif.data.User;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;
import ru.prohor.universe.scarif.oauth.exception.OAuthClientErrorException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;
import ru.prohor.universe.scarif.services.session.SessionData;
import ru.prohor.universe.scarif.services.session.SessionsService;
import ru.prohor.universe.scarif.web.UserData;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.CLIENT_ID;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.booleanClaim;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.claim;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.properties;

class GoogleOAuthServiceTest {
    private final MongoRepository<User> usersRepository = mockMongoRepository();
    private final UserService userService = mock(UserService.class);
    private final GoogleOAuthClient googleOAuthClient = mock(GoogleOAuthClient.class);
    private final GoogleIdTokenVerifier googleIdTokenVerifier = mock(GoogleIdTokenVerifier.class);
    private final SessionsService sessionsService = mock(SessionsService.class);
    private final CookieProvider cookieProvider = mock(CookieProvider.class);
    private final String frontendHost = "https://app.example.com";

    private GoogleOAuthService service;

    @SuppressWarnings("unchecked")
    private MongoRepository<User> mockMongoRepository() {
        return mock(MongoRepository.class);
    }

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            MonoFunction<MongoRepository<User>, ?> transaction = invocation.getArgument(0);
            return transaction.apply(usersRepository);
        }).when(usersRepository).withTransaction(
                ArgumentMatchers.<MonoFunction<MongoRepository<User>, Object>>any()
        );
        service = new GoogleOAuthService(
                usersRepository, userService, googleOAuthClient, googleIdTokenVerifier,
                sessionsService, cookieProvider, properties(), frontendHost
        );
    }

    private static void stubStringClaim(DecodedJWT jwt, String name, String value) {
        Claim claim = claim(value);
        when(jwt.getClaim(name)).thenReturn(claim);
    }

    private static void stubEmailVerifiedClaim(DecodedJWT jwt, Boolean value) {
        Claim claim = booleanClaim(value);
        when(jwt.getClaim("email_verified")).thenReturn(claim);
    }

    private static Session testSession() {
        return new Session(
                ObjectId.get(), Instant.now(), Instant.now().plusSeconds(600),
                Opt.empty(), "127.0.0.1", false, Opt.empty(), List.of(), Opt.of(ObjectId.get())
        );
    }

    @Test
    void redirectsToGoogleWhenNotLoggedIn() {
        ResponseEntity<?> response = service.redirectToGoogleLoginPage(Opt.empty(), Opt.empty());

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().contains("client_id=" + CLIENT_ID));
        assertTrue(location.toString().contains("response_type=code"));
    }

    @Test
    void redirectsToFrontendWhenRefreshTokenPresent() {
        RefreshToken refreshToken = new RefreshToken(ObjectId.get(), ObjectId.get(), ObjectId.get());

        ResponseEntity<?> response = service.redirectToGoogleLoginPage(Opt.of(refreshToken), Opt.empty());

        assertEquals(URI.create(frontendHost), response.getHeaders().getLocation());
        verifyNoInteractions(googleOAuthClient);
    }

    @Test
    void redirectsToFrontendWhenAuthorizedUserPresent() {
        AuthorizedUser user = new AuthorizedUser(1L, UUID.randomUUID(), "obj-id");

        ResponseEntity<?> response = service.redirectToGoogleLoginPage(Opt.empty(), Opt.of(user));

        assertEquals(URI.create(frontendHost), response.getHeaders().getLocation());
    }

    @Test
    void authorizeRedirectsIfAlreadyLoggedIn() {
        AuthorizedUser user = new AuthorizedUser(1L, UUID.randomUUID(), "obj-id");
        UserData userData = new UserData("127.0.0.1", Opt.empty());

        ResponseEntity<?> response = service.authorize(Opt.empty(), Opt.of(user), "code", userData);

        assertEquals(URI.create(frontendHost), response.getHeaders().getLocation());
        verifyNoInteractions(googleOAuthClient, googleIdTokenVerifier, sessionsService, usersRepository);
    }

    @Test
    void authorizeCreatesNewUserAndSession() throws Exception {
        when(googleOAuthClient.getIdToken("auth-code")).thenReturn("id-token");

        DecodedJWT decoded = mock(DecodedJWT.class);
        stubStringClaim(decoded, "sub", "google-sub-1");
        stubStringClaim(decoded, "email", "user@example.com");
        stubStringClaim(decoded, "name", "Test User");
        stubStringClaim(decoded, "given_name", "Test");
        stubStringClaim(decoded, "family_name", "User");
        stubStringClaim(decoded, "picture", "https://pic");
        stubStringClaim(decoded, "locale", "ru");
        stubEmailVerifiedClaim(decoded, true);
        when(googleIdTokenVerifier.verify("id-token")).thenReturn(decoded);

        when(userService.findByExternalAccount(any(), eq(ExternalAccountProvider.GOOGLE), eq("google-sub-1")))
                .thenReturn(Opt.empty());
        User newUser = new User(ObjectId.get(), UUID.randomUUID(), 42L, Instant.now(), List.of(), List.of());
        when(userService.createUser()).thenReturn(newUser);

        when(sessionsService.createNewSession(any(), any()))
                .thenReturn(new SessionData(testSession(), "refresh-jwt", "access-jwt"));
        when(cookieProvider.createRefreshCookie("refresh-jwt")).thenReturn("refresh-cookie-value");

        UserData userData = new UserData("127.0.0.1", Opt.of("test-agent"));
        ResponseEntity<?> response = service.authorize(Opt.empty(), Opt.empty(), "auth-code", userData);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create(frontendHost), response.getHeaders().getLocation());
        List<String> setCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookie);
        assertTrue(setCookie.stream().anyMatch(c -> c.contains("refresh-cookie-value")));

        verify(userService).createUser();
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(savedUser.capture());
        User saved = savedUser.getValue();
        assertEquals(1, saved.externalAccounts().size());
        ExternalAccount account = saved.externalAccounts().getFirst();
        assertEquals("google-sub-1", account.id());
        assertEquals(ExternalAccountProvider.GOOGLE, account.provider());
        assertTrue(account.primary());
        assertEquals("user@example.com", account.email().get());
        assertEquals(1, saved.sessions().size());
    }

    @Test
    void authorizeReplacesExistingGoogleAccountRatherThanDuplicatingIt() throws Exception {
        when(googleOAuthClient.getIdToken("auth-code")).thenReturn("id-token");

        DecodedJWT decoded = mock(DecodedJWT.class);
        stubStringClaim(decoded, "sub", "google-sub-1");
        stubStringClaim(decoded, "email", "new-email@example.com");
        stubStringClaim(decoded, "name", null);
        stubStringClaim(decoded, "given_name", null);
        stubStringClaim(decoded, "family_name", null);
        stubStringClaim(decoded, "picture", null);
        stubStringClaim(decoded, "locale", null);
        stubEmailVerifiedClaim(decoded, true);
        when(googleIdTokenVerifier.verify("id-token")).thenReturn(decoded);

        ExternalAccount existingGoogleAccount = new ExternalAccount(
                "google-sub-1", ExternalAccountProvider.GOOGLE, true,
                Opt.of("old-email@example.com"), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty()
        );
        User existingUser = new User(
                ObjectId.get(), UUID.randomUUID(), 7L, Instant.now(),
                List.of(existingGoogleAccount), List.of()
        );
        when(userService.findByExternalAccount(any(), eq(ExternalAccountProvider.GOOGLE), eq("google-sub-1")))
                .thenReturn(Opt.of(existingUser));

        when(sessionsService.createNewSession(any(), any()))
                .thenReturn(new SessionData(testSession(), "refresh-jwt", "access-jwt"));
        when(cookieProvider.createRefreshCookie("refresh-jwt")).thenReturn("cookie");

        UserData userData = new UserData("127.0.0.1", Opt.empty());
        service.authorize(Opt.empty(), Opt.empty(), "auth-code", userData);

        verify(userService, never()).createUser();
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(usersRepository).save(savedUser.capture());
        User saved = savedUser.getValue();
        assertEquals(1, saved.externalAccounts().size(), "google-аккаунт должен быть заменён, а не задублирован");
        assertEquals("new-email@example.com", saved.externalAccounts().getFirst().email().get());
    }

    @Test
    void authorizeRedirectsWithClientErrorWhenEmailNotVerified() throws Exception {
        when(googleOAuthClient.getIdToken("auth-code")).thenReturn("id-token");
        DecodedJWT decoded = mock(DecodedJWT.class);
        stubEmailVerifiedClaim(decoded, false);
        when(googleIdTokenVerifier.verify("id-token")).thenReturn(decoded);

        UserData userData = new UserData("127.0.0.1", Opt.empty());

        ResponseEntity<?> response = assertDoesNotThrow(
                () -> service.authorize(Opt.empty(), Opt.empty(), "auth-code", userData),
                "errorRedirect должен вернуть редирект для кириллического сообщения"
        );

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("type=client-error"));
    }

    @Test
    void authorizeKeepsAmpersandInsideErrorMessage() throws Exception {
        String clientMessage = "Ошибка &retry=true";
        when(googleOAuthClient.getIdToken("auth-code"))
                .thenThrow(new OAuthClientErrorException("test error", clientMessage));

        ResponseEntity<?> response = service.authorize(
                Opt.empty(), Opt.empty(), "auth-code", new UserData("127.0.0.1", Opt.empty())
        );

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(location).build().getQueryParams();
        assertEquals(2, params.size());
        assertEquals("client-error", params.getFirst("type"));
        String actualMessage = params.getFirst("message");
        assertNotNull(actualMessage);
        assertEquals(clientMessage, URLDecoder.decode(actualMessage, StandardCharsets.UTF_8));
    }

    @Test
    void authorizeRedirectsWithServerErrorWhenVerificationFails() throws Exception {
        when(googleOAuthClient.getIdToken("auth-code")).thenReturn("id-token");
        when(googleIdTokenVerifier.verify("id-token"))
                .thenThrow(new OAuthServerErrorException("signature invalid"));

        UserData userData = new UserData("127.0.0.1", Opt.empty());

        ResponseEntity<?> response = assertDoesNotThrow(
                () -> service.authorize(Opt.empty(), Opt.empty(), "auth-code", userData)
        );

        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("type=server-error"));
    }

    @Test
    void authorizeRedirectsWithServerErrorOnUnexpectedException() throws Exception {
        when(googleOAuthClient.getIdToken("auth-code")).thenThrow(new RuntimeException("boom"));

        UserData userData = new UserData("127.0.0.1", Opt.empty());

        ResponseEntity<?> response = assertDoesNotThrow(
                () -> service.authorize(Opt.empty(), Opt.empty(), "auth-code", userData)
        );

        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("type=server-error"));
    }
}
