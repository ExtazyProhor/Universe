package ru.prohor.universe.scarif.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.hyperspace.jwtprovider.JwtProvider;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

@Configuration
public class JwtProviderConfiguration {
    @Bean
    public JwtProvider jwtProvider(
            @Value("${universe.scarif.accessTokenTtlMinutes}") int accessTokenTtlMinutes,
            SnowflakeIdGenerator snowflakeIdGenerator,
            KeysFromStringProvider keysFromStringProvider,
            ObjectMapper objectMapper
    ) {
        return new JwtProvider(
                accessTokenTtlMinutes,
                snowflakeIdGenerator,
                keysFromStringProvider,
                objectMapper
        );
    }
}
