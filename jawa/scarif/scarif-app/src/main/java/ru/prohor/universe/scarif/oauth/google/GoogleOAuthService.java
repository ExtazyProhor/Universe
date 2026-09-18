package ru.prohor.universe.scarif.oauth.google;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.springweb.CookieUtil;
import ru.prohor.universe.scarif.data.ExternalAccount;
import ru.prohor.universe.scarif.data.ExternalAccountProvider;
import ru.prohor.universe.scarif.data.Session;
import ru.prohor.universe.scarif.data.User;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;
import ru.prohor.universe.scarif.oauth.UserExternalAccountInfo;
import ru.prohor.universe.scarif.oauth.exception.OAuthClientErrorException;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;
import ru.prohor.universe.scarif.services.session.SessionData;
import ru.prohor.universe.scarif.services.session.SessionsService;
import ru.prohor.universe.scarif.web.UserData;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleOAuthService {
    private final MongoRepository<User> usersRepository;
    private final UserService userService;
    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final SessionsService sessionsService;
    private final CookieProvider cookieProvider;
    private final URI googleLoginUrl;
    private final String frontendHost;

    public GoogleOAuthService(
            MongoRepository<User> usersRepository,
            UserService userService,
            GoogleOAuthClient googleOAuthClient,
            GoogleIdTokenVerifier googleIdTokenVerifier,
            SessionsService sessionsService,
            CookieProvider cookieProvider,
            GoogleOAuthProperties googleOAuthProperties,
            @Value("${universe.scarif.frontend-host}") String frontendHost
    ) {
        this.usersRepository = usersRepository;
        this.userService = userService;
        this.googleOAuthClient = googleOAuthClient;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.sessionsService = sessionsService;
        this.cookieProvider = cookieProvider;
        this.googleLoginUrl = UriComponentsBuilder
                .fromUriString(googleOAuthProperties.authUrl())
                .queryParam("client_id", googleOAuthProperties.clientId())
                .queryParam("redirect_uri", googleOAuthProperties.redirectUrl())
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .build()
                .toUri();
        this.frontendHost = frontendHost;
    }

    public ResponseEntity<?> redirectToGoogleLoginPage(
            Opt<RefreshToken> refreshToken,
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (refreshToken.isPresent() || authorizedUser.isPresent()) {
            // TODO log
            System.out.println("Already logged in 4321");
            return redirectToMain().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(googleLoginUrl).build();
    }

    // TODO обобщить
    private ResponseEntity.BodyBuilder redirectToMain() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(frontendHost));
    }

    public ResponseEntity<?> authorize(
            Opt<RefreshToken> refreshToken,
            Opt<AuthorizedUser> authorizedUser,
            String code,
            UserData userData
    ) {
        if (refreshToken.isPresent() || authorizedUser.isPresent()) {
            // TODO log
            System.out.println("Already logged in");
            return redirectToMain().build();
        }

        try {
            UserExternalAccountInfo userInfo = getUserInfoByCode(code);
            HttpHeaders headers = createOrUpdateUser(userInfo, userData);
            return redirectToMain().headers(headers).build();
        } catch (OAuthException e) {
            return switch (e) {
                case OAuthClientErrorException err -> errorRedirect("client-error", err.getMessage());
                case OAuthServerErrorException err -> {
                    // TODO log
                    err.printStackTrace();
                    yield errorRedirect("server-error", "Ошибка сервера при аутентификации");
                }
            };
        } catch (Exception e) {
            // TODO log
            e.printStackTrace();
            return errorRedirect("server-error", "Ошибка сервера");
        }
    }

    private ResponseEntity<?> errorRedirect(String type, String message) {
        URI frontendMainPage = UriComponentsBuilder.fromUriString(frontendHost)
                .queryParam("type", type)
                .queryParam("message", message)
                .build()
                .toUri();
        return ResponseEntity.status(HttpStatus.FOUND).location(frontendMainPage).build();
    }

    private HttpHeaders createOrUpdateUser(UserExternalAccountInfo userInfo, UserData userData) {
        return usersRepository.withTransaction(repo -> {
            Opt<User> userO = userService.findByExternalAccount(repo, ExternalAccountProvider.GOOGLE, userInfo.id());
            boolean primary = userO.isEmpty() || userO.get().externalAccounts().stream()
                    .anyMatch(acc -> acc.provider() == ExternalAccountProvider.GOOGLE && acc.primary());
            ExternalAccount googleAccount = new ExternalAccount(
                    userInfo.id(),
                    ExternalAccountProvider.GOOGLE,
                    primary,
                    userInfo.email(),
                    userInfo.name(),
                    userInfo.givenName(),
                    userInfo.familyName(),
                    userInfo.pictureUrl(),
                    userInfo.locale()
            );
            User user = userO.orElseGet(userService::createUser);
            List<ExternalAccount> externalAccounts = new ArrayList<>(
                    user.externalAccounts().stream()
                            .filter(it -> it.provider() != ExternalAccountProvider.GOOGLE)
                            .toList()
            );
            externalAccounts.add(googleAccount);

            SessionData sessionData = sessionsService.createNewSession(user, userData);
            List<Session> sessions = new ArrayList<>(user.sessions());
            sessions.add(sessionData.session());

            user = user.toBuilder()
                    .externalAccounts(externalAccounts)
                    .sessions(sessions)
                    .build();

            repo.save(user);
            return CookieUtil.setCookieHeader(cookieProvider.createRefreshCookie(sessionData.refreshJwt()));
        });
    }

    private UserExternalAccountInfo getUserInfoByCode(String code) throws OAuthException {
        String idToken = googleOAuthClient.getIdToken(code);
        DecodedJWT jwt = googleIdTokenVerifier.verify(idToken);
        return new UserExternalAccountInfo(
                jwt.getClaim("sub").asString(),
                Opt.ofNullable(jwt.getClaim("email").asString()),
                Opt.ofNullable(jwt.getClaim("name").asString()),
                Opt.ofNullable(jwt.getClaim("given_name").asString()),
                Opt.ofNullable(jwt.getClaim("family_name").asString()),
                Opt.ofNullable(jwt.getClaim("picture").asString()),
                Opt.ofNullable(jwt.getClaim("locale").asString())
        );
    }
}
