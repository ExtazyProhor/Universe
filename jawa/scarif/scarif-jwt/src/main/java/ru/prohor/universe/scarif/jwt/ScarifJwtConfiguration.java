package ru.prohor.universe.scarif.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

@Configuration
@Import(JacksonJocastaCoreConfiguration.class)
@ComponentScan
public class ScarifJwtConfiguration {
    @Bean
    public AccessJwtVerifier jwtVerifier(
            PublicKeyProvider publicKeyProvider,
            ObjectMapper objectMapper
    ) {
        return new AccessJwtVerifier(publicKeyProvider, objectMapper);
    }

    @Bean
    public FilterRegistrationBean<AccessTokenFilter> accessTokenFilterRegistration(AccessJwtVerifier accessJwtVerifier) {
        FilterRegistrationBean<AccessTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AccessTokenFilter(accessJwtVerifier));
        registration.addUrlPatterns("/*");
        registration.setOrder(AccessTokenFilter.ACCESS_TOKEN_FILTER_ORDER);
        return registration;
    }
}
