package ru.prohor.universe.hyperspace.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;
import ru.prohor.universe.jocasta.jackson.jodatime.JacksonJocastaCoreConfiguration;

@Configuration
@Import(JacksonJocastaCoreConfiguration.class)
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
