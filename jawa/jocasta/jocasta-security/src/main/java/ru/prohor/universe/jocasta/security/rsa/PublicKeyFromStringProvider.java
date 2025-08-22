package ru.prohor.universe.jocasta.security.rsa;

import org.apache.commons.codec.binary.Base64;

import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyFromStringProvider implements PublicKeyProvider {
    private final RSAPublicKey key;

    public PublicKeyFromStringProvider(String key) {
        try {
            byte[] decoded = Base64.decodeBase64(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            this.key = (RSAPublicKey) KeyFactoryProvider.rsa().generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            throw new RSAKeyCreatingException(e);
        }
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return key;
    }
}
