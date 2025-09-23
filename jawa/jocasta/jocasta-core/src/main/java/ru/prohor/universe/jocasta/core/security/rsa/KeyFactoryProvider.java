package ru.prohor.universe.jocasta.core.security.rsa;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

public final class KeyFactoryProvider {
    private KeyFactoryProvider() {}

    // TODO lazy
    private static final KeyFactory RSA;

    static {
        try {
            RSA = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm RSA not found", e);
        }
    }

    public static KeyFactory rsa() {
        return RSA;
    }
}
