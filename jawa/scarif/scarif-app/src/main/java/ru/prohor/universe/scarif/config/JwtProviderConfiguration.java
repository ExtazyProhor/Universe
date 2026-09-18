package ru.prohor.universe.scarif.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.scarif.jwtprovider.AccessJwtProvider;

import java.time.Duration;

@Configuration
public class JwtProviderConfiguration {
    @Bean
    public AccessJwtProvider jwtProvider(
            @Value("${universe.scarif.access-token.ttl}") Duration accessTokenTtl,
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        return new AccessJwtProvider(
                accessTokenTtl,
                keysFromStringProvider,
                objectMapper
        );
    }
}
