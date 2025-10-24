package ru.prohor.universe.yahtzee.app.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyFromStringProvider;

@Configuration
public class KeysConfiguration {
    @Bean
    public PublicKeyFromStringProvider keysFromStringProvider(
            @Value("${universe.yahtzee.public-key}") String publicKey
    ) {
        return new PublicKeyFromStringProvider(publicKey);
    }
}
