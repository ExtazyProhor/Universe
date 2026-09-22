package ru.prohor.universe.scarif.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractJwtVerifierTest {
    private final AbstractJwtVerifier subject = new AbstractJwtVerifier() {};
    private final JWTVerifier jwtVerifier = mock(JWTVerifier.class);

    @Test
    void returnsDecodedJwtWhenVerificationSucceeds() throws Exception {
        DecodedJWT decoded = mock(DecodedJWT.class);
        when(jwtVerifier.verify("token")).thenReturn(decoded);

        assertSame(decoded, subject.verify(jwtVerifier, "token"));
    }

    @Test
    void expiredTokenIsNotAWarning() {
        when(jwtVerifier.verify("token")).thenThrow(mock(TokenExpiredException.class));

        JwtVerificationException e = assertThrows(
                JwtVerificationException.class,
                () -> subject.verify(jwtVerifier, "token")
        );

        assertFalse(e.isWarning());
        assertEquals("jwt token was expired", e.getMessage());
        assertNull(e.getCause());
    }

    static Stream<Throwable> unexpectedFailures() {
        return Stream.of(
                new AlgorithmMismatchException("wrong algorithm"),
                new SignatureVerificationException(Algorithm.none()),
                new JWTDecodeException("bad format"),
                new RuntimeException("something else")
        );
    }

    @ParameterizedTest
    @MethodSource("unexpectedFailures")
    void otherFailuresAreWarningsAndKeepCause(Throwable failure) {
        when(jwtVerifier.verify("token")).thenThrow(failure);

        JwtVerificationException e = assertThrows(
                JwtVerificationException.class,
                () -> subject.verify(jwtVerifier, "token")
        );

        assertTrue(e.isWarning());
        assertSame(failure, e.getCause());
    }

    @Test
    void jwtVerificationExceptionFlags() {
        Throwable cause = new IllegalStateException("x");

        JwtVerificationException withCause = new JwtVerificationException(cause);
        assertTrue(withCause.isWarning());
        assertSame(cause, withCause.getCause());

        JwtVerificationException withMessage = new JwtVerificationException("msg");
        assertFalse(withMessage.isWarning());
        assertEquals("msg", withMessage.getMessage());
        assertNull(withMessage.getCause());
    }
}
