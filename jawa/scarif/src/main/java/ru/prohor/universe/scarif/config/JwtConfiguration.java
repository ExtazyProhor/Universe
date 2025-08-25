package ru.prohor.universe.scarif.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.scarifJwt.AccessTokenFilter;
import ru.prohor.universe.jocasta.scarifJwt.JwtVerifier;
import ru.prohor.universe.jocasta.security.rsa.KeysFromStringProvider;

@Configuration
public class JwtConfiguration {
    @Bean
    public JwtVerifier jwtVerifier(
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        return new JwtVerifier(keysFromStringProvider, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterRegistration(
            @Value("${universe.scarif.accessTokenCookieName}") String accessTokenCookieName,
            JwtVerifier jwtVerifier
    ) {
        FilterRegistrationBean<AccessTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessTokenFilter(accessTokenCookieName, jwtVerifier));
        registration.addUrlPatterns("/*");
        return registration;
    }
}
