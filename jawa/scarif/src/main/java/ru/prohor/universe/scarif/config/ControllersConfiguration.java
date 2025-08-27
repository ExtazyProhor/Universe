package ru.prohor.universe.scarif.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.spring.configuration.AllControllersConfiguration;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.scarif.services.JwtProvider;
import ru.prohor.universe.scarif.services.LoginService;
import ru.prohor.universe.scarif.services.RateLimitService;
import ru.prohor.universe.scarif.services.RegistrationService;
import ru.prohor.universe.scarif.services.SessionsService;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.web.AuthController;

@Configuration
@Import({
        AllControllersConfiguration.class
})
public class ControllersConfiguration {
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
