package ru.prohor.universe.scarif.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;
import ru.prohor.universe.scarif.services.session.SessionsService;
import ru.prohor.universe.scarif.web.api.CloseSessionRequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final SessionsService sessionsService;

    public AuthController(SessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (authorizedUser.isPresent()) {
            log.warn("endpoint /refresh was called manually, access token already present");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return sessionsService.refresh(refreshToken.get());
    }

    @GetMapping("/get_sessions")
    public ResponseEntity<?> getSessions(
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (refreshToken.isEmpty() || authorizedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return sessionsService.getSessions(refreshToken.get());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken
    ) {
        if (refreshToken.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return sessionsService.logout(refreshToken.get());
    }

    @PostMapping("/close_session")
    public ResponseEntity<?> closeSession(
            @RequestBody
            CloseSessionRequestBody body,
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        if (refreshToken.isEmpty() || authorizedUser.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return sessionsService.closeSession(refreshToken.get(), body);
    }
}
