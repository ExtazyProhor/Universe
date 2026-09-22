package ru.prohor.universe.scarif.jwt;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.MDC;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.JWT_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.NUMERIC_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.OBJECT_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.SESSION_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.UUID_VALUE;

class AccessJwtVerifierTest {
    private LogCaptor accessLogs;
    private LogCaptor staticKeyLogs;
    private AccessJwtVerifier verifier;

    @BeforeEach
    void setUp() {
        MDC.clear();
        accessLogs = LogCaptor.of(AccessJwtVerifier.class);
        staticKeyLogs = LogCaptor.of(StaticKeyJwtVerifier.class);
        verifier = JwtTestSupport.accessJwtVerifier();
    }

    @AfterEach
    void tearDown() {
        accessLogs.close();
        staticKeyLogs.close();
        MDC.clear();
    }

    @Test
    void validTokenReturnsAuthorizedUser() {
        Opt<AuthorizedUser> result = verifier.verify(JwtTestSupport.validAccessToken());

        assertTrue(result.isPresent());
        AuthorizedUser user = result.get();
        assertEquals(NUMERIC_ID, user.numericId());
        assertEquals(UUID_VALUE, user.uuid());
        assertEquals(OBJECT_ID, user.objectId());
    }

    @Test
    void validTokenPutsSessionIdToMdc() {
        verifier.verify(JwtTestSupport.validAccessToken());
        assertEquals(SESSION_ID, MDC.get(MDCFields.SESSION_ID_KEY));
    }

    @Test
    void validTokenLogsJwtIdOnTraceAndNothingElse() {
        verifier.verify(JwtTestSupport.validAccessToken());
        assertTrue(accessLogs.has(Level.TRACE, "received accessToken with id " + JWT_ID));
        assertEquals(1, accessLogs.all().size());
        assertTrue(staticKeyLogs.all().isEmpty());
    }

    @Test
    void expiredTokenReturnsEmptyWithTraceLogOnly() {
        Opt<AuthorizedUser> result = verifier.verify(JwtTestSupport.expiredAccessToken());

        assertTrue(result.isEmpty());
        assertTrue(staticKeyLogs.has(Level.TRACE, "access jwt: jwt token was expired"));
        assertTrue(staticKeyLogs.at(Level.WARN).isEmpty());
        assertTrue(staticKeyLogs.at(Level.ERROR).isEmpty());
        assertTrue(accessLogs.all().isEmpty());
        assertNull(MDC.get(MDCFields.SESSION_ID_KEY));
    }

    static Stream<Arguments> invalidTokens() {
        return Stream.of(
                Arguments.of(
                        "signed with another key",
                        JwtTestSupport.tokenSignedWithOtherKey(),
                        SignatureVerificationException.class
                ),
                Arguments.of(
                        "payload swapped, signature kept",
                        JwtTestSupport.tokenWithSwappedPayload(),
                        SignatureVerificationException.class
                ),
                Arguments.of(
                        "HS256 instead of RS256",
                        JwtTestSupport.hs256Token(),
                        AlgorithmMismatchException.class
                ),
                Arguments.of(
                        "alg none",
                        JwtTestSupport.noneAlgorithmToken(),
                        AlgorithmMismatchException.class
                ),
                Arguments.of(
                        "three parts but garbage",
                        "not.a.jwt",
                        JWTDecodeException.class
                ),
                Arguments.of(
                        "no dots at all",
                        "garbage",
                        JWTDecodeException.class
                ),
                Arguments.of(
                        "empty string",
                        "",
                        JWTDecodeException.class
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTokens")
    void invalidTokenReturnsEmptyAndLogsWarningWithCause(
            String name,
            String token,
            Class<? extends Throwable> expectedCause
    ) {
        Opt<AuthorizedUser> result = verifier.verify(token);
        assertTrue(result.isEmpty());

        List<ILoggingEvent> warnings = staticKeyLogs.at(Level.WARN);
        assertEquals(1, warnings.size());
        ILoggingEvent warning = warnings.getFirst();
        assertEquals("Error when verifying access jwt", warning.getFormattedMessage());
        assertNotNull(warning.getThrowableProxy());
        assertEquals(JwtVerificationException.class.getName(), warning.getThrowableProxy().getClassName());
        assertNotNull(warning.getThrowableProxy().getCause());
        assertEquals(expectedCause.getName(), warning.getThrowableProxy().getCause().getClassName());

        assertTrue(accessLogs.all().isEmpty());
        assertNull(MDC.get(MDCFields.SESSION_ID_KEY));
    }

    @Test
    void validSignatureWithBrokenPayloadThrows() {
        String token = JwtTestSupport.validSignatureBrokenPayloadToken();
        assertThrows(Exception.class, () -> verifier.verify(token));

        assertTrue(staticKeyLogs.at(Level.WARN).isEmpty());
        assertTrue(accessLogs.all().isEmpty());
        assertNull(MDC.get(MDCFields.SESSION_ID_KEY));
    }
}
