package ru.prohor.universe.jocasta.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

@Configuration
public class ScarifJwtConfiguration {
    @Bean
    public JwtVerifier jwtVerifier(
            PublicKeyProvider publicKeyProvider,
            ObjectMapper objectMapper
    ) {
        return new JwtVerifier(publicKeyProvider, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterRegistration(
            @Value("${universe.jocasta.scarif-jwt.accessTokenCookieName}") String accessTokenCookieName,
            JwtVerifier jwtVerifier
    ) {
        FilterRegistrationBean<AccessTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessTokenFilter(accessTokenCookieName, jwtVerifier));
        registration.addUrlPatterns("/*");
        registration.setOrder(AccessTokenFilter.ACCESS_TOKEN_FILTER_ORDER);
        return registration;
    }
}
