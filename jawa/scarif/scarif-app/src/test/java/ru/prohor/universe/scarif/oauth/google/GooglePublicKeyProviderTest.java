package ru.prohor.universe.scarif.oauth.google;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.generateKeyPair;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.jwk;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.jwkSet;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.mockRestClientForGet;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.mockRestClientForGetThrowing;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.properties;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.rsaJwk;

class GooglePublicKeyProviderTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void returnsMatchingKeyFromJwkSet() throws Exception {
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey expected = (RSAPublicKey) keyPair.getPublic();
        RestClient restClient = mockRestClientForGet(jwkSet(rsaJwk("kid-1", expected)));

        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());
        RSAPublicKey actual = provider.getPublicKey("kid-1");

        assertEquals(expected.getModulus(), actual.getModulus());
        assertEquals(expected.getPublicExponent(), actual.getPublicExponent());
    }

    @Test
    void nonRsaKeysAreSkippedButRsaKeysStillWork() throws Exception {
        KeyPair keyPair = generateKeyPair();
        JsonNode body = jwkSet(
                jwk("ec-kid", "EC", null),
                rsaJwk("rsa-kid", (RSAPublicKey) keyPair.getPublic())
        );
        RestClient restClient = mockRestClientForGet(body);
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        assertThrows(OAuthException.class, () -> provider.getPublicKey("ec-kid"));
        assertEquals(
                ((RSAPublicKey) keyPair.getPublic()).getModulus(),
                provider.getPublicKey("rsa-kid").getModulus()
        );
    }

    @Test
    void onlyNonRsaKeysGivesNoRsaKeysError() {
        RestClient restClient = mockRestClientForGet(jwkSet(jwk("ec-kid", "EC", null)));
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("ec-kid"));
        assertInstanceOf(OAuthServerErrorException.class, e);
        assertTrue(e.getMessage().contains("no RSA keys"));
    }

    @Test
    void unknownKeyIdThrowsAfterRefreshingKeys() {
        KeyPair keyPair = generateKeyPair();
        RestClient restClient = mockRestClientForGet(jwkSet(rsaJwk("kid-1", (RSAPublicKey) keyPair.getPublic())));
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("unknown-kid"));
        assertTrue(e.getMessage().contains("unknown-kid"));
        verify(restClient, times(1)).get();
    }

    @Test
    void nullResponseThrows() {
        RestClient restClient = mockRestClientForGet((JsonNode) null);
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("any"));
        assertTrue(e.getMessage().contains("Google JWK Set is empty"));
    }

    @Test
    void responseWithoutKeysFieldThrows() {
        RestClient restClient = mockRestClientForGet(MAPPER.createObjectNode());
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("any"));
        assertTrue(e.getMessage().contains("Google JWK Set is empty"));
    }

    @Test
    void missingModulusFieldThrows() {
        ObjectNode brokenJwk = MAPPER.createObjectNode();
        brokenJwk.put("kid", "kid-1");
        brokenJwk.put("kty", "RSA");
        brokenJwk.put("e", "AQAB");
        RestClient restClient = mockRestClientForGet(jwkSet(brokenJwk));
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("kid-1"));
        assertTrue(e.getMessage().contains("missing field: n"));
    }

    @Test
    void restClientFailureIsWrappedWithCause() {
        RestClientException cause = new RestClientException("boom");
        RestClient restClient = mockRestClientForGetThrowing(cause);
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> provider.getPublicKey("any"));
        assertInstanceOf(OAuthServerErrorException.class, e);
        assertEquals(cause, e.getCause());
    }

    @Test
    void keyIsCachedWithinTtl() throws Exception {
        KeyPair keyPair = generateKeyPair();
        RestClient restClient = mockRestClientForGet(jwkSet(rsaJwk("kid-1", (RSAPublicKey) keyPair.getPublic())));
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties(Duration.ofMinutes(10)));

        provider.getPublicKey("kid-1");
        provider.getPublicKey("kid-1");
        provider.getPublicKey("kid-1");

        verify(restClient, times(1)).get();
    }

    @Test
    void keysAreReloadedAfterTtlExpires() throws Exception {
        KeyPair keyPair = generateKeyPair();
        RestClient restClient = mockRestClientForGet(jwkSet(rsaJwk("kid-1", (RSAPublicKey) keyPair.getPublic())));
        GooglePublicKeyProvider provider = new GooglePublicKeyProvider(restClient, properties(Duration.ofMillis(1)));

        provider.getPublicKey("kid-1");
        Thread.sleep(20);
        provider.getPublicKey("kid-1");

        verify(restClient, times(2)).get();
    }
}
