package ru.prohor.universe.scarif.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

@Configuration
public class KeysConfiguration {
    @Bean
    public KeysFromStringProvider keysFromStringProvider(
            @Value("${universe.scarif.private-key}") String privateKey,
            @Value("${universe.scarif.public-key}") String publicKey
    ) {
        return new KeysFromStringProvider(privateKey, publicKey);
    }
}
