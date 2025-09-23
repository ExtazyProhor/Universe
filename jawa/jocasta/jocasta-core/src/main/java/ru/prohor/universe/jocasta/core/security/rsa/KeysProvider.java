package ru.prohor.universe.jocasta.core.security.rsa;

import java.security.interfaces.RSAPrivateKey;

public interface KeysProvider extends PublicKeyProvider {
    RSAPrivateKey getPrivateKey();
}
