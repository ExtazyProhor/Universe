package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.prohor.universe.jocasta.core.jackson.JacksonConfiguration;
import ru.prohor.universe.jocasta.core.jackson.JacksonJocastaCoreConfiguration;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

final class JwtTestSupport {
    static final long NUMERIC_ID = 42L;
    static final UUID UUID_VALUE = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    static final String OBJECT_ID = "690335fa9ba3639211b39386";
    static final String SESSION_ID = "690335fa9ba3639211b39387";
    static final String JWT_ID = "690335fa9ba3639211b39388";

    /**
     * Ключ, которому доверяет верификатор.
     */
    static final KeyPair KEY_PAIR = generateKeyPair();
    /**
     * Чужой ключ, для проверки неверной подписи.
     */
    static final KeyPair OTHER_KEY_PAIR = generateKeyPair();

    private JwtTestSupport() {}

    static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static PublicKeyProvider publicKeyProvider() {
        return () -> (RSAPublicKey) KEY_PAIR.getPublic();
    }

    static ObjectMapper objectMapper() {
        return new JacksonConfiguration().objectMapper(List.of(
                new JacksonJocastaCoreConfiguration().jocastaCoreModule()
        ));
    }

    static AccessJwtVerifier accessJwtVerifier() {
        return new AccessJwtVerifier(publicKeyProvider(), objectMapper());
    }

    static JWTCreator.Builder accessTokenBuilder(String objectId, Instant expires) {
        return JWT.create()
                .withSubject(String.valueOf(NUMERIC_ID))
                .withClaim("uid", UUID_VALUE.toString())
                .withClaim("oid", objectId)
                .withClaim("sid", SESSION_ID)
                .withJWTId(JWT_ID)
                .withExpiresAt(expires);
    }

    static String sign(JWTCreator.Builder builder, KeyPair keyPair) {
        return builder.sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    static String validAccessToken() {
        return sign(accessTokenBuilder(OBJECT_ID, Instant.now().plusSeconds(300)), KEY_PAIR);
    }

    static String expiredAccessToken() {
        return sign(accessTokenBuilder(OBJECT_ID, Instant.now().minusSeconds(300)), KEY_PAIR);
    }

    static String tokenSignedWithOtherKey() {
        return sign(accessTokenBuilder(OBJECT_ID, Instant.now().plusSeconds(300)), OTHER_KEY_PAIR);
    }

    static String hs256Token() {
        return accessTokenBuilder(OBJECT_ID, Instant.now().plusSeconds(300)).sign(Algorithm.HMAC256("secret"));
    }

    static String noneAlgorithmToken() {
        return accessTokenBuilder(OBJECT_ID, Instant.now().plusSeconds(300)).sign(Algorithm.none());
    }

    /**
     * Заголовок и подпись от одного токена, payload - от другого (с другим oid)
     */
    static String tokenWithSwappedPayload() {
        String[] original = validAccessToken().split("\\.");
        String[] foreign = sign(
                accessTokenBuilder("obj-attacker", Instant.now().plusSeconds(300)),
                KEY_PAIR
        ).split("\\.");
        return original[0] + "." + foreign[1] + "." + original[2];
    }

    /**
     * Подпись валидна, но payload не маппится в AccessJwtPayload (sub - не число)
     */
    static String validSignatureBrokenPayloadToken() {
        return sign(
                accessTokenBuilder(OBJECT_ID, Instant.now().plusSeconds(300)).withSubject("not-a-number"),
                KEY_PAIR
        );
    }
}
