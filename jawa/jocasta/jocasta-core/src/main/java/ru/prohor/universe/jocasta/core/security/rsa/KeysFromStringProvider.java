package ru.prohor.universe.jocasta.core.security.rsa;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class KeysFromStringProvider implements KeysProvider {
    private final PrivateKeyFromStringProvider privateKeyFromStringProvider;
    private final PublicKeyFromStringProvider publicKeyFromStringProvider;

    public KeysFromStringProvider(String privateKey, String publicKey) {
        this.privateKeyFromStringProvider = new PrivateKeyFromStringProvider(privateKey);
        this.publicKeyFromStringProvider = new PublicKeyFromStringProvider(publicKey);
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKeyFromStringProvider.getPrivateKey();
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKeyFromStringProvider.getPublicKey();
    }
}
