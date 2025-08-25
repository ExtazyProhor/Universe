package ru.prohor.universe.scarif.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.JwtProvider;
import ru.prohor.universe.scarif.services.LoginService;
import ru.prohor.universe.scarif.services.RateLimitService;
import ru.prohor.universe.scarif.services.RegistrationService;
import ru.prohor.universe.scarif.services.SessionsService;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.web.AuthController;
import ru.prohor.universe.scarif.web.StaticController;

@Configuration
public class ControllersConfiguration {
    @Bean
    public StaticController staticController(
            CookieProvider cookieProvider,
            @Value("${universe.scarif.local.requests:#{null}}") String requestsPath,
            @Value("${universe.scarif.root}") String root,
            @Value("${universe.scarif.index}") String index,
            @Value("${universe.scarif.filesCacheMaxAgeDays}") int filesCacheMaxAgeDays
    ) {
        return new StaticController(cookieProvider, requestsPath, root, index, filesCacheMaxAgeDays);
    }

    @Bean
    public AuthController authController(
            RegistrationService registrationService,
            RateLimitService rateLimitService,
            SessionsService sessionsService,
            CookieProvider cookieProvider,
            LoginService loginService,
            JwtProvider jwtProvider,
            UserService userService
    ) {
        return new AuthController(
                registrationService,
                rateLimitService,
                sessionsService,
                cookieProvider,
                loginService,
                jwtProvider,
                userService
        );
    }
}
