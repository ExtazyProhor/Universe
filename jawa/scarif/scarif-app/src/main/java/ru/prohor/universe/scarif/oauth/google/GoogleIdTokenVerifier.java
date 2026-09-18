package ru.prohor.universe.scarif.oauth.google;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import ru.prohor.universe.scarif.jwt.JwtVerificationException;
import ru.prohor.universe.scarif.oauth.exception.OAuthClientErrorException;
import ru.prohor.universe.scarif.oauth.exception.OAuthException;
import ru.prohor.universe.scarif.oauth.exception.OAuthServerErrorException;

import java.security.interfaces.RSAPublicKey;

@Service
public class GoogleIdTokenVerifier {
    private final GooglePublicKeyProvider googlePublicKeyProvider;
    private final String issuer;
    private final String clientId;

    public GoogleIdTokenVerifier(
            GooglePublicKeyProvider googlePublicKeyProvider,
            GoogleOAuthProperties googleOAuthProperties
    ) {
        this.googlePublicKeyProvider = googlePublicKeyProvider;
        this.issuer = googleOAuthProperties.issuer();
        this.clientId = googleOAuthProperties.clientId();
    }

    public DecodedJWT verify(String token) throws OAuthException {
        DecodedJWT decoded = JWT.decode(token);
        String keyId = decoded.getKeyId();
        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("Google ID token does not contain kid");
        }

        RSAPublicKey publicKey = googlePublicKeyProvider.getPublicKey(keyId);
        Algorithm algorithm = Algorithm.RSA256(publicKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(clientId)
                .build();

        try {
            DecodedJWT verified = JwtVerificationException.verify(verifier, token);
            Boolean emailVerified = verified.getClaim("email_verified").asBoolean();
            System.out.println("email verified: " + emailVerified); // TODO log

            if (!verified.getClaim("email_verified").asBoolean()) {
                // TODO сделать разделение на ошибки сервера и клиента.
                //  В случае неподтвержденной почты виноват клиент
                throw new OAuthClientErrorException("Google email is not verified");
            }
            return verified;
        } catch (JwtVerificationException e) {
            throw new OAuthServerErrorException("Error verifying Google tokenId JWT", e);
        }
    }
}
