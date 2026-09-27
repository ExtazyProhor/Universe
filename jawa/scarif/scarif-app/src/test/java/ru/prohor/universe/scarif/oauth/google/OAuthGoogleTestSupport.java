package ru.prohor.universe.scarif.oauth.google;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.client.RestClient;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class OAuthGoogleTestSupport {
    static final String ISSUER = "https://issuer.example.test";
    static final String CLIENT_ID = "test-client-id";
    static final String JWK_SET_URL = "https://keys.example.test/jwks";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OAuthGoogleTestSupport() {}

    static GoogleOAuthProperties properties() {
        return properties(Duration.ofMinutes(10));
    }

    static GoogleOAuthProperties properties(Duration jwkSetCacheTtl) {
        return new GoogleOAuthProperties(
                CLIENT_ID,
                "test-client-secret",
                "https://accounts.example.test/oauth/authorize",
                "https://backend.example.com/api/oauth/google/callback",
                "https://accounts.example.test/oauth/token",
                JWK_SET_URL,
                jwkSetCacheTtl,
                ISSUER
        );
    }

    static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static RestClient mockRestClientForGet(JsonNode responseBody) {
        return mockRestClientForGet(() -> responseBody);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static RestClient mockRestClientForGet(Supplier<JsonNode> responseSupplier) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(JsonNode.class)).thenAnswer(_ -> responseSupplier.get());

        return restClient;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static RestClient mockRestClientForGetThrowing(RuntimeException exception) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestHeadersUriSpec uriSpec = mock(RestClient.RequestHeadersUriSpec.class);

        when(restClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenThrow(exception);

        return restClient;
    }

    static RestClient mockRestClientForPost(JsonNode responseBody) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(any())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(any(Object.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(JsonNode.class)).thenReturn(responseBody);

        return restClient;
    }

    static RestClient mockRestClientForPostThrowing(RuntimeException exception) {
        RestClient restClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec bodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);

        when(restClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(any())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.body(any(Object.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenThrow(exception);

        return restClient;
    }

    static byte[] unsignedBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        }
        return bytes;
    }

    static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static ObjectNode jwk(String kid, String kty, RSAPublicKey key) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("kid", kid);
        node.put("kty", kty);
        if (key != null) {
            node.put("n", base64Url(unsignedBytes(key.getModulus())));
            node.put("e", base64Url(unsignedBytes(key.getPublicExponent())));
        }
        return node;
    }

    static ObjectNode rsaJwk(String kid, RSAPublicKey key) {
        return jwk(kid, "RSA", key);
    }

    static JsonNode jwkSet(JsonNode... keys) {
        ObjectNode root = MAPPER.createObjectNode();
        ArrayNode array = root.putArray("keys");
        for (JsonNode key : keys) {
            array.add(key);
        }
        return root;
    }

    static String googleIdToken(
            KeyPair keyPair,
            String kid,
            String issuer,
            String audience,
            Instant expires,
            boolean emailVerified,
            String subject
    ) {
        JWTCreator.Builder builder = JWT.create()
                .withKeyId(kid)
                .withIssuer(issuer)
                .withAudience(audience)
                .withExpiresAt(expires)
                .withSubject(subject)
                .withClaim("email", subject + "@example.com")
                .withClaim("email_verified", emailVerified)
                .withClaim("name", "Test User")
                .withClaim("given_name", "Test")
                .withClaim("family_name", "User")
                .withClaim("picture", "https://example.com/pic.png")
                .withClaim("locale", "ru");
        return builder.sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    static String googleIdToken(KeyPair keyPair, String kid) {
        return googleIdToken(
                keyPair, kid, ISSUER, CLIENT_ID,
                Instant.now().plusSeconds(300), true, "google-subject-1"
        );
    }

    static Claim claim(String value) {
        Claim c = mock(Claim.class);
        when(c.asString()).thenReturn(value);
        return c;
    }

    static Claim booleanClaim(Boolean value) {
        Claim c = mock(Claim.class);
        when(c.asBoolean()).thenReturn(value);
        return c;
    }
}
