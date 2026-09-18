package ru.prohor.universe.scarif.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;
import ru.prohor.universe.scarif.oauth.google.GoogleOAuthService;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {
    private final GoogleOAuthService googleOAuthService;

    public OAuthController(GoogleOAuthService googleOAuthService) {
        this.googleOAuthService = googleOAuthService;
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser,
            @RequestAttribute(name = UserData.USER_DATA_ATTRIBUTE_KEY)
            UserData userData,
            @RequestParam("code")
            String code
    ) {
        return googleOAuthService.authorize(refreshToken, authorizedUser, code, userData);
    }

    @GetMapping("/google")
    public ResponseEntity<?> google(
            @RequestAttribute(name = RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY)
            Opt<RefreshToken> refreshToken,
            @RequestAttribute(name = AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY)
            Opt<AuthorizedUser> authorizedUser
    ) {
        return googleOAuthService.redirectToGoogleLoginPage(refreshToken, authorizedUser);
    }
}
