package ru.prohor.universe.scarif.oauth.google;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.prohor.universe.jocasta.core.security.rsa.KeyFactoryProvider;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class GooglePublicKeyProvider {
    private final RestClient restClient;
    private final String jwkSetUrl;
    private final Duration cacheTtl;

    private volatile Cache cache = Cache.empty();

    public GooglePublicKeyProvider(RestClient restClient, GoogleOAuthProperties googleOAuthProperties) {
        this.restClient = restClient;
        this.jwkSetUrl = googleOAuthProperties.jwkSetUrl();
        this.cacheTtl = googleOAuthProperties.jwkSetCacheTtl();
    }

    public RSAPublicKey getPublicKey(String keyId) throws OAuthException {
        Cache current = cache;
        if (current.isNotExpired(cacheTtl)) {
            RSAPublicKey key = current.keys().get(keyId);
            if (key != null)
                return key;
        }

        synchronized (this) {
            current = cache;
            if (current.isNotExpired(cacheTtl)) {
                RSAPublicKey key = current.keys().get(keyId);
                if (key != null)
                    return key;
            }

            Cache refreshed = loadKeys();
            cache = refreshed;
            RSAPublicKey key = refreshed.keys().get(keyId);

            if (key == null) {
                throw new OAuthServerErrorException("Unknown Google key: " + keyId);
            }
            return key;
        }
    }

    private Cache loadKeys() throws OAuthException {
        JsonNode jwkSet;
        try {
            jwkSet = restClient.get().uri(jwkSetUrl).retrieve().body(JsonNode.class);
        } catch (Exception e) {
            throw new OAuthServerErrorException("Error when calling google jwk", e); // TODO
        }

        if (jwkSet == null || !jwkSet.has("keys")) {
            throw new OAuthServerErrorException("Google JWK Set is empty");
        }

        Map<String, RSAPublicKey> newKeys = new HashMap<>();
        for (JsonNode jwk : jwkSet.get("keys")) {
            String keyId = requiredText(jwk, "kid");
            String keyType = requiredText(jwk, "kty");
            if (!"RSA".equals(keyType)) {
                continue;
            }
            newKeys.put(keyId, rsaPublicKey(jwk));
        }

        if (newKeys.isEmpty()) {
            throw new OAuthServerErrorException("Google JWK Set contains no RSA keys");
        }
        return new Cache(Map.copyOf(newKeys), Instant.now());
    }

    private RSAPublicKey rsaPublicKey(JsonNode jwk) throws OAuthException {
        String modulus = requiredText(jwk, "n");
        String exponent = requiredText(jwk, "e");
        byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);
        BigInteger n = new BigInteger(1, modulusBytes);
        BigInteger e = new BigInteger(1, exponentBytes);

        try {
            KeyFactory factory = KeyFactoryProvider.rsa();
            return (RSAPublicKey) factory.generatePublic(new RSAPublicKeySpec(n, e));
        } catch (GeneralSecurityException exception) {
            throw new OAuthServerErrorException("Cannot create RSA public key", exception);
        }
    }

    private String requiredText(JsonNode node, String field) throws OAuthException {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new OAuthServerErrorException("Google JWK is missing field: " + field);
        }
        return value.asText();
    }

    private record Cache(Map<String, RSAPublicKey> keys, Instant loadedAt) {
        private static Cache empty() {
            return new Cache(Map.of(), Instant.EPOCH);
        }

        private boolean isNotExpired(Duration ttl) {
            return Instant.now().isBefore(loadedAt.plus(ttl));
        }
    }
}
