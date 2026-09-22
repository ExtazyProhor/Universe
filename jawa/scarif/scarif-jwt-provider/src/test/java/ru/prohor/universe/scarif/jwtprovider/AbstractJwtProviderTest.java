package ru.prohor.universe.scarif.jwtprovider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.security.rsa.KeysFromStringProvider;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тестирует AbstractJwtProvider изолированно от AccessJwtPayload/AccessJwtProvider,
 * через минимальный тестовый payload и тестовый наследник
 */
class AbstractJwtProviderTest {
    private record TestPayload(@JsonProperty("foo") String foo, @JsonProperty("num") int num) {}

    private static class TestJwtProvider extends AbstractJwtProvider<TestPayload> {
        TestJwtProvider(KeysFromStringProvider keysFromStringProvider, ObjectMapper objectMapper) {
            super(keysFromStringProvider, objectMapper);
        }

        String buildToken(TestPayload payload) {
            return getToken(payload);
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KeyPair keyPair = JwtProviderTestSupport.generateKeyPair();
    private final TestJwtProvider provider = new TestJwtProvider(
            JwtProviderTestSupport.keysProvider(keyPair),
            objectMapper
    );

    @Test
    void tokenHasThreeSegmentsAndRs256Header() {
        String token = provider.buildToken(new TestPayload("bar", 1));

        assertEquals(3, token.split("\\.", -1).length);
        assertEquals("RS256", JWT.decode(token).getAlgorithm());
    }

    @Test
    void tokenIsVerifiableOnlyWithMatchingPublicKey() {
        String token = provider.buildToken(new TestPayload("bar", 1));

        Algorithm matching = Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), null);
        DecodedJWT decoded = assertDoesNotThrow(() -> JWT.require(matching).build().verify(token));
        assertNotNull(decoded);

        KeyPair otherPair = JwtProviderTestSupport.generateKeyPair();
        Algorithm mismatched = Algorithm.RSA256((RSAPublicKey) otherPair.getPublic(), null);
        assertThrows(Exception.class, () -> JWT.require(mismatched).build().verify(token));
    }

    @Test
    void payloadIsExactlyTheObjectMapperSerialization() throws Exception {
        TestPayload payload = new TestPayload("bar", 42);
        String token = provider.buildToken(payload);

        String expectedJson = objectMapper.writeValueAsString(payload);
        String actualJson = new String(Base64.decodeBase64(JWT.decode(token).getPayload()));

        assertEquals(expectedJson, actualJson);
    }

    @Test
    void differentPayloadsProduceDifferentTokens() {
        String tokenA = provider.buildToken(new TestPayload("a", 1));
        String tokenB = provider.buildToken(new TestPayload("b", 1));

        org.junit.jupiter.api.Assertions.assertNotEquals(tokenA, tokenB);
    }
}
