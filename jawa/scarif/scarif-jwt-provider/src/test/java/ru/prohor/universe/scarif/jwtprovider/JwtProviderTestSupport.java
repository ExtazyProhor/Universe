package ru.prohor.universe.scarif.jwtprovider;

import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class JwtProviderTestSupport {
    private JwtProviderTestSupport() {}

    static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static KeysFromStringProvider keysProvider(KeyPair keyPair) {
        KeysFromStringProvider provider = mock(KeysFromStringProvider.class);
        when(provider.getPrivateKey()).thenReturn((RSAPrivateKey) keyPair.getPrivate());
        when(provider.getPublicKey()).thenReturn((RSAPublicKey) keyPair.getPublic());
        return provider;
    }
}
