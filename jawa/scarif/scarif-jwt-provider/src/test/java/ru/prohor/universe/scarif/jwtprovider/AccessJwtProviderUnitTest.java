package ru.prohor.universe.scarif.jwtprovider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;
import ru.prohor.universe.scarif.jwt.AccessJwtPayload;
import ru.prohor.universe.scarif.jwt.AccessJwtVerifier;
import ru.prohor.universe.scarif.jwt.AuthorizedUser;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юнит-тесты AccessJwtProvider без Spring-контекста и без Holocron: ключи
 * генерируются на лету и подставляются через mock KeysFromStringProvider (см.
 * JwtProviderTestSupport). Дополняет уже существующий интеграционный
 * AccessJwtProviderTest, который гоняет полный цикл через реальный контекст
 * и настоящие ключи
 */
class AccessJwtProviderUnitTest {
    private static final Duration TTL = Duration.ofMinutes(1);

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final KeyPair keyPair = JwtProviderTestSupport.generateKeyPair();
    private final KeysFromStringProvider keysFromStringProvider = JwtProviderTestSupport.keysProvider(keyPair);

    private final long numericId = 123L;
    private final UUID uuid = UUID.fromString("f7739220-b874-4209-928b-9e1c2aee8e6c");
    private final ObjectId objectId = new ObjectId("690335fa9ba3639211b393aa");
    private final ObjectId sessionId = new ObjectId("690335fa9ba3639211b393ab");

    private AccessJwtProvider provider;
    private AccessJwtVerifier verifier;

    @BeforeEach
    void setUp() {
        provider = new AccessJwtProvider(TTL, keysFromStringProvider, objectMapper);
        verifier = new AccessJwtVerifier(keysFromStringProvider, objectMapper);
    }

    private AccessJwtPayload decode(String token) throws Exception {
        DecodedJWT decoded = JWT.decode(token);
        String json = new String(Base64.decodeBase64(decoded.getPayload()));
        return objectMapper.readValue(json, AccessJwtPayload.class);
    }

    @Test
    void tokenRoundTripsThroughVerifier() {
        String token = provider.getToken(numericId, uuid, objectId, sessionId);

        Opt<AuthorizedUser> result = verifier.verify(token);

        assertTrue(result.isPresent());
        AuthorizedUser user = result.get();
        assertEquals(numericId, user.numericId());
        assertEquals(uuid, user.uuid());
        assertEquals(objectId, new ObjectId(user.objectId()));
    }

    @Test
    void expiryIsApproximatelyNowPlusTtl() throws Exception {
        Instant before = Instant.now();
        String token = provider.getToken(numericId, uuid, objectId, sessionId);
        Instant after = Instant.now();

        AccessJwtPayload payload = decode(token);

        assertFalse(payload.expires().isBefore(before.plus(TTL)));
        assertFalse(payload.expires().isAfter(after.plus(TTL).plusSeconds(1)));
    }

    @Test
    void expiredTokenIsRejectedByVerifier() throws Exception {
        AccessJwtProvider shortLivedProvider =
                new AccessJwtProvider(Duration.ofMillis(1), keysFromStringProvider, objectMapper);
        String token = shortLivedProvider.getToken(numericId, uuid, objectId, sessionId);

        Thread.sleep(50);

        assertTrue(verifier.verify(token).isEmpty());
    }

    @Test
    void eachTokenGetsAUniqueJwtId() throws Exception {
        String tokenA = provider.getToken(numericId, uuid, objectId, sessionId);
        String tokenB = provider.getToken(numericId, uuid, objectId, sessionId);

        assertNotEquals(decode(tokenA).jwtId(), decode(tokenB).jwtId());
    }

    @Test
    void sessionIdAndJwtIdAreNotSwapped() throws Exception {
        String token = provider.getToken(numericId, uuid, objectId, sessionId);

        AccessJwtPayload payload = decode(token);

        assertEquals(
                sessionId.toHexString(), payload.sessionId(),
                "payload.sessionId() должен содержать hex сессии, а не сгенерированный jwtId"
        );
        assertNotEquals(
                sessionId.toHexString(), payload.jwtId(),
                "payload.jwtId() совпал с sessionId - поля перепутаны местами"
        );
    }
}