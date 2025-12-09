package ru.prohor.universe.scarif.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.hyperspace.jwt.AuthorizedUser;
import ru.prohor.universe.hyperspace.jwtprovider.JwtProvider;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.jodatime.DateTimeUtil;
import ru.prohor.universe.scarif.data.session.Session;
import ru.prohor.universe.scarif.data.user.User;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.LoginService;
import ru.prohor.universe.scarif.services.ratelimit.LoginIpRateLimiter;
import ru.prohor.universe.scarif.services.RegistrationService;
import ru.prohor.universe.scarif.services.SessionsService;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.services.ratelimit.RegisterIpRateLimiter;
import ru.prohor.universe.scarif.services.refresh.ParsedUserToken;
import ru.prohor.universe.scarif.services.refresh.UserTokenAndUserId;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RegisterIpRateLimiter registerIpRateLimiter;
    private final RegistrationService registrationService;
    private final LoginIpRateLimiter loginIpRateLimiter;
    private final SessionsService sessionsService;
    private final CookieProvider cookieProvider;
    private final LoginService loginService;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public AuthController(
            RegisterIpRateLimiter registerIpRateLimiter,
            RegistrationService registrationService,
            LoginIpRateLimiter loginIpRateLimiter,
            SessionsService sessionsService,
            CookieProvider cookieProvider,
            LoginService loginService,
            JwtProvider jwtProvider,
            UserService userService
    ) {
        this.registerIpRateLimiter = registerIpRateLimiter;
        this.registrationService = registrationService;
        this.loginIpRateLimiter = loginIpRateLimiter;
        this.sessionsService = sessionsService;
        this.cookieProvider = cookieProvider;
        this.loginService = loginService;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @PostMapping("/register") // TODO подтверждение по почте (защита от автоматической регистрации)
    public ResponseEntity<?> register(
            @RequestBody
            RegisterRequestBody body,
            @RequestAttribute(name = Requests.USER_DATA_ATTRIBUTE_KEY)
            UserData userData,
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (!registerIpRateLimiter.isAllowed(userData.ip()))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();

        if (parsedUserToken.isPresent() || authorizedUser.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        try {
            User user = registrationService.registerUser(body.username, body.email, body.password);
            registerIpRateLimiter.reset(userData.ip());
            return newSession(user, userData);
        } catch (RegistrationService.RegistrationException e) {
            return ResponseEntity.badRequest().body(e.cause);
        }
    }

    public record RegisterRequestBody(
            String username,
            String email,
            String password
    ) {} // TODO OpenAPI

    @PostMapping("/login") // TODO Уведомления пользователя о новых сессиях, или о попытках входа
    public ResponseEntity<?> login(
            @RequestBody
            LoginRequestBody body,
            @RequestAttribute(name = Requests.USER_DATA_ATTRIBUTE_KEY)
            UserData userData,
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (!loginIpRateLimiter.isAllowed(userData.ip()))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build(); // TODO CAPTCHA, 2FA (TOTP)

        if (parsedUserToken.isPresent() || authorizedUser.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        try {
            User user = loginService.authenticateUser(body.login, body.password);
            loginIpRateLimiter.reset(userData.ip());
            return newSession(user, userData);
        } catch (LoginService.LoginException e) {
            log.debug("exception at loginService", e);
            return ResponseEntity.status(e.status).body(e.cause);
        }
    }

    public record LoginRequestBody(
            String login,
            String password
    ) {} // TODO OpenAPI

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (authorizedUser.isPresent()) {
            log.warn("[SB] endpoint /refresh was called manually, access token already present");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (parsedUserToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Opt<UserTokenAndUserId> userTokenAndUserId = sessionsService.refreshToken(parsedUserToken.get());
        if (userTokenAndUserId.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Opt<User> userO = userService.find(userTokenAndUserId.get().userId());
        if (userO.isEmpty()) {
            log.error(
                    "Token was successfully refreshed, but the owner (user) of this token was not found, userId = {}",
                    userTokenAndUserId.get().userId()
            );
            return clearCookies(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return fromCookies(List.of(
                cookieProvider.createRefreshCookie(userTokenAndUserId.get().userToken()),
                cookieProvider.createAccessCookie(userO.map(this::getToken).get())
        ));
    }

    @PostMapping("/logout") // TODO разобраться с HTTP-методами
    public ResponseEntity<?> logout(
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken
    ) {
        if (parsedUserToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (!sessionsService.logout(parsedUserToken.get()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return clearCookies(); // TODO redirect to login
    }

    @GetMapping("/get_sessions")
    public ResponseEntity<?> getSessions(
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken
    ) {
        if (parsedUserToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Opt<Session> currentSession = sessionsService.getSession(parsedUserToken.get());
        if (currentSession.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(
                sessionsService.getAllValidSessions(currentSession.get().userId())
                        .stream()
                        .map(session -> new SessionResponseBody(
                                session.ipAddress(),
                                session.userAgent().orElseNull(),
                                session.id(),
                                DateTimeUtil.toReadableString(session.createdAt()),
                                session.id() == currentSession.get().id()
                        )).toList()
        );
    }

    public record SessionResponseBody(String ip, String userAgent, long id, String start, boolean current) {}

    @PostMapping("/close_session")
    public ResponseEntity<?> closeSession(
            @RequestBody
            CloseSessionRequestBody body,
            @RequestAttribute(name = Requests.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<ParsedUserToken> parsedUserToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser

    ) {
        if (parsedUserToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (authorizedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Opt<Session> currentSession = sessionsService.getSession(parsedUserToken.get());
        Opt<Session> session = sessionsService.getSession(body.id());
        if (session.isEmpty() || session.get().userId() != authorizedUser.get().id())
            return ResponseEntity.notFound().build();
        sessionsService.closeSession(session.get());

        if (session.get().id() != currentSession.get().id())
            return ResponseEntity.ok().build();
        return clearCookies();
    }

    @GetMapping("/clear_cookies")
    public ResponseEntity<?> closeSession() {
        return clearCookies();
    }

    public record CloseSessionRequestBody(long id) {}

    private ResponseEntity<?> newSession(User user, UserData userData) {
        String token = sessionsService.createNewSession(
                user.id(),
                userData.userAgent().orElseNull(),
                userData.ip()
        );
        return fromCookies(List.of(
                cookieProvider.createRefreshCookie(token),
                cookieProvider.createAccessCookie(getToken(user))
        ));
    }

    private ResponseEntity<?> fromCookies(List<String> cookies) {
        return fromCookies(cookies, HttpStatus.OK);
    }

    private ResponseEntity<?> clearCookies() {
        return clearCookies(HttpStatus.OK);
    }

    private ResponseEntity<?> clearCookies(HttpStatus status) {
        return fromCookies(
                List.of(
                        cookieProvider.clearRefreshCookie(),
                        cookieProvider.clearAccessCookie()
                ),
                status
        );
    }

    private ResponseEntity<?> fromCookies(List<String> cookies, HttpStatus status) {
        return ResponseEntity.status(status).headers(
                new HttpHeaders(MultiValueMap.fromMultiValue(Map.of(
                        HttpHeaders.SET_COOKIE,
                        cookies
                )))
        ).build();
    }

    private String getToken(User user) {
        return jwtProvider.getToken(
                user.id(),
                user.uuid(),
                user.objectId().toString(),
                user.username()
        );
    }
}
