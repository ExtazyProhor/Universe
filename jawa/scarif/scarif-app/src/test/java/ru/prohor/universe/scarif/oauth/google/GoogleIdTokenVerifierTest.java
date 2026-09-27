package ru.prohor.universe.scarif.oauth.google;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.CLIENT_ID;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.ISSUER;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.generateKeyPair;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.googleIdToken;
import static ru.prohor.universe.scarif.oauth.google.OAuthGoogleTestSupport.properties;

class GoogleIdTokenVerifierTest {
    private static final String KID = "kid-1";

    private GoogleIdTokenVerifier verifierWithKey(KeyPair keyPair) throws Exception {
        GooglePublicKeyProvider publicKeyProvider = mock(GooglePublicKeyProvider.class);
        when(publicKeyProvider.getPublicKey(KID)).thenReturn((RSAPublicKey) keyPair.getPublic());
        return new GoogleIdTokenVerifier(publicKeyProvider, properties());
    }

    @Test
    void validTokenIsVerified() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(keyPair, "kid-1");
        GoogleIdTokenVerifier verifier = verifierWithKey(keyPair);

        DecodedJWT decoded = verifier.verify(token);

        assertEquals("google-subject-1", decoded.getSubject());
    }

    @Test
    void publicKeyIsLookedUpByKid() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(keyPair, "the-kid");
        GooglePublicKeyProvider publicKeyProvider = mock(GooglePublicKeyProvider.class);
        when(publicKeyProvider.getPublicKey("the-kid")).thenReturn((RSAPublicKey) keyPair.getPublic());
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(publicKeyProvider, properties());

        verifier.verify(token);

        verify(publicKeyProvider).getPublicKey("the-kid");
    }

    @Test
    void missingKidThrowsWithoutCallingKeyProvider() {
        KeyPair keyPair = generateKeyPair();
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withAudience(CLIENT_ID)
                .withExpiresAt(Instant.now().plusSeconds(60))
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));

        GooglePublicKeyProvider publicKeyProvider = mock(GooglePublicKeyProvider.class);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(publicKeyProvider, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> verifier.verify(token));
        assertTrue(e.getMessage().contains("kid"));
        verifyNoInteractions(publicKeyProvider);
    }

    @Test
    void wrongSignatureIsRejected() throws Exception {
        KeyPair signingKey = generateKeyPair();
        KeyPair keyProviderKey = generateKeyPair();
        String token = googleIdToken(signingKey, "kid-1");

        GooglePublicKeyProvider publicKeyProvider = mock(GooglePublicKeyProvider.class);
        when(publicKeyProvider.getPublicKey("kid-1")).thenReturn((RSAPublicKey) keyProviderKey.getPublic());
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(publicKeyProvider, properties());

        OAuthException e = assertThrows(OAuthException.class, () -> verifier.verify(token));
        assertInstanceOf(OAuthServerErrorException.class, e);
        assertNotNull(e.getCause());
    }

    @Test
    void wrongIssuerIsRejected() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(
                keyPair, "kid-1", "https://not-google.example", CLIENT_ID,
                Instant.now().plusSeconds(60), true, "sub"
        );
        GoogleIdTokenVerifier verifier = verifierWithKey(keyPair);

        assertThrows(OAuthException.class, () -> verifier.verify(token));
    }

    @Test
    void wrongAudienceIsRejected() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(
                keyPair, "kid-1", ISSUER, "someone-elses-client-id",
                Instant.now().plusSeconds(60), true, "sub"
        );
        GoogleIdTokenVerifier verifier = verifierWithKey(keyPair);

        assertThrows(OAuthException.class, () -> verifier.verify(token));
    }

    @Test
    void expiredTokenIsRejected() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(
                keyPair, "kid-1", ISSUER, CLIENT_ID,
                Instant.now().minusSeconds(60), true, "sub"
        );
        GoogleIdTokenVerifier verifier = verifierWithKey(keyPair);

        OAuthException e = assertThrows(OAuthException.class, () -> verifier.verify(token));
        assertInstanceOf(OAuthServerErrorException.class, e);
    }

    @Test
    void keyProviderExceptionPropagatesUnwrapped() throws Exception {
        KeyPair keyPair = generateKeyPair();
        String token = googleIdToken(keyPair, "unknown-kid");

        GooglePublicKeyProvider publicKeyProvider = mock(GooglePublicKeyProvider.class);
        OAuthServerErrorException notFound = new OAuthServerErrorException("Unknown Google key: unknown-kid");
        when(publicKeyProvider.getPublicKey("unknown-kid")).thenThrow(notFound);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier(publicKeyProvider, properties());

        OAuthException thrown = assertThrows(OAuthException.class, () -> verifier.verify(token));
        assertSame(notFound, thrown, "исключение от GooglePublicKeyProvider не должно повторно оборачиваться");
    }
}
