package ru.prohor.universe.jocasta.security.rsa;

import java.security.interfaces.RSAPrivateKey;

public interface KeysProvider extends PublicKeyProvider {
    RSAPrivateKey getPrivateKey();
}
