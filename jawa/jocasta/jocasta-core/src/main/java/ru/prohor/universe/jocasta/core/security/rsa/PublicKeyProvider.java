package ru.prohor.universe.jocasta.core.security.rsa;

import java.security.interfaces.RSAPublicKey;

public interface PublicKeyProvider {
    RSAPublicKey getPublicKey();
}
