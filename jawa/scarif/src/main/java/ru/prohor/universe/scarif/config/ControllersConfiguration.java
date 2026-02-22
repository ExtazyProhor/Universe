package ru.prohor.universe.scarif.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.springweb.configuration.AllControllersConfiguration;
import ru.prohor.universe.scarif.services.CookieProvider;
import ru.prohor.universe.hyperspace.jwtprovider.JwtProvider;
import ru.prohor.universe.scarif.services.LoginService;
import ru.prohor.universe.scarif.services.ratelimit.LoginIpRateLimiter;
import ru.prohor.universe.scarif.services.RegistrationService;
import ru.prohor.universe.scarif.services.SessionsService;
import ru.prohor.universe.scarif.services.UserService;
import ru.prohor.universe.scarif.services.ratelimit.RegisterIpRateLimiter;
import ru.prohor.universe.scarif.web.AuthController;

@Configuration
@Import({
        AllControllersConfiguration.class
})
public class ControllersConfiguration {
    @Bean
    public AuthController authController(
            RegisterIpRateLimiter registerIpRateLimiter,
            RegistrationService registrationService,
            LoginIpRateLimiter loginIpRateLimiter,
            SessionsService sessionsService,
            CookieProvider cookieProvider,
            LoginService loginService,
            JwtProvider jwtProvider,
            UserService userService
    ) {
        return new AuthController(
                registerIpRateLimiter,
                registrationService,
                loginIpRateLimiter,
                sessionsService,
                cookieProvider,
                loginService,
                jwtProvider,
                userService
        );
    }
}
