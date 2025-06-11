package ru.prohor.universe.jocasta.security;

import org.apache.commons.codec.digest.DigestUtils;

public final class CryptoUtil {
    private CryptoUtil() {}

    public static String sha256(String s) {
        // и прочие вариации
        return DigestUtils.sha256Hex(s);
    }
}
