package ru.prohor.universe.jocasta.security.rsa;

import java.security.spec.InvalidKeySpecException;

public class RSAKeyCreatingException extends RuntimeException {
    public RSAKeyCreatingException(InvalidKeySpecException cause) {
        super("Error creating RSA key", cause);
    }
}
