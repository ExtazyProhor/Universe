package ru.prohor.universe.probe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

@TestConfiguration
public class TestKeysFromStringProviderConfiguration {
    @Bean
    public KeysFromStringProvider keysFromStringProvider(
            @Value("${universe.test.private-key}") String privateKey,
            @Value("${universe.test.public-key}") String publicKey
    ) {
        return new KeysFromStringProvider(privateKey, publicKey);
    }
}
