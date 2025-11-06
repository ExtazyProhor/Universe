package ru.prohor.universe.venator.webhook;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignatureService {
    private final HmacUtils hmacUtils;

    public SignatureService(@Value("${universe.venator.webhook.secret}") String secret) {
        this.hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret);
    }

    public boolean verifySignature(String signature, String body) {
        if (signature == null)
            return false;
        String hmacHex = hmacUtils.hmacHex(body);
        String expectedSignature = "sha256=" + hmacHex;
        return signature.equals(expectedSignature);
    }
}
