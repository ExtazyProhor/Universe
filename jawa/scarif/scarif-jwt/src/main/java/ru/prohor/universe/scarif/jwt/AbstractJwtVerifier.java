package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

public abstract class AbstractJwtVerifier {
    protected DecodedJWT verify(JWTVerifier verifier, String token) throws JwtVerificationException {
        try {
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new JwtVerificationException("jwt token was expired");
        } catch (Exception e) {
            // неверный алгоритм подписи - AlgorithmMismatchException
            // неверная подпись - SignatureVerificationException
            // неверный формат JWT или JSON - JWTDecodeException
            throw new JwtVerificationException(e);
        }
    }
}
