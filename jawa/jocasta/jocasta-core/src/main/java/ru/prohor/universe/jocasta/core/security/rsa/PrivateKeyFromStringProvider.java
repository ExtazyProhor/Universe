package ru.prohor.universe.jocasta.core.security.rsa;

import org.apache.commons.codec.binary.Base64;

import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PrivateKeyFromStringProvider {
    private final RSAPrivateKey key;

    public PrivateKeyFromStringProvider(String key) {
        try {
            byte[] decoded = Base64.decodeBase64(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            this.key = (RSAPrivateKey) KeyFactoryProvider.rsa().generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            throw new RSAKeyCreatingException(e);
        }
    }

    public RSAPrivateKey getPrivateKey() {
        return key;
    }
}
