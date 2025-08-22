package ru.prohor.universe.jocasta.security.rsa;

import java.security.interfaces.RSAPublicKey;

public interface PublicKeyProvider {
    RSAPublicKey getPublicKey();
}
