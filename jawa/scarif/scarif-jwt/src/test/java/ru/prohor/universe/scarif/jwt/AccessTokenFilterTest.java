package ru.prohor.universe.scarif.jwt;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.NUMERIC_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.OBJECT_ID;
import static ru.prohor.universe.scarif.jwt.JwtTestSupport.UUID_VALUE;

class AccessTokenFilterTest {
    private LogCaptor filterLogs;
    private LogCaptor verifierLogs;
    private AccessTokenFilter filter;

    @BeforeEach
    void setUp() {
        MDC.clear();
        filterLogs = LogCaptor.of(AccessTokenFilter.class);
        verifierLogs = LogCaptor.of(StaticKeyJwtVerifier.class);
        filter = new AccessTokenFilter(JwtTestSupport.accessJwtVerifier());
    }

    @AfterEach
    void tearDown() {
        filterLogs.close();
        verifierLogs.close();
        MDC.clear();
    }

    private static final class CapturingChain implements FilterChain {
        boolean invoked;
        String userIdInMdc;
        String sessionIdInMdc;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            invoked = true;
            userIdInMdc = MDC.get(MDCFields.USER_ID_KEY);
            sessionIdInMdc = MDC.get(MDCFields.SESSION_ID_KEY);
        }
    }

    private static MockHttpServletRequest requestWithAuth(String headerValue) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (headerValue != null) {
            request.addHeader(HttpHeaders.AUTHORIZATION, headerValue);
        }
        return request;
    }

    @SuppressWarnings("unchecked")
    private static Opt<AuthorizedUser> authorizedUser(MockHttpServletRequest request) {
        Object attribute = request.getAttribute(AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY);
        assertNotNull(attribute, "attribute must always be set on the non-error path");
        return (Opt<AuthorizedUser>) attribute;
    }

    @Test
    void noAuthHeaderProceedsAnonymously() throws Exception {
        MockHttpServletRequest request = requestWithAuth(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, response, chain);

        assertTrue(authorizedUser(request).isEmpty());
        assertTrue(chain.invoked);
        assertEquals(200, response.getStatus());
        assertNull(chain.userIdInMdc);
        assertTrue(filterLogs.has(Level.TRACE, "Auth header not present"));
        assertTrue(filterLogs.at(Level.WARN).isEmpty());
    }

    @Test
    void nonBearerHeaderProceedsAnonymouslyWithWarning() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Basic dXNlcjpwYXNz");
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(authorizedUser(request).isEmpty());
        assertTrue(chain.invoked);
        assertTrue(filterLogs.has(Level.WARN, "Illegal structure of auth header"));
    }

    @Test
    void validBearerSetsAttributeAndMdc() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Bearer " + JwtTestSupport.validAccessToken());
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, response, chain);

        Opt<AuthorizedUser> user = authorizedUser(request);
        assertTrue(user.isPresent());
        assertEquals(NUMERIC_ID, user.get().numericId());
        assertEquals(UUID_VALUE, user.get().uuid());
        assertEquals(OBJECT_ID, user.get().objectId());

        assertTrue(chain.invoked);
        assertEquals(200, response.getStatus());
        assertEquals(OBJECT_ID, chain.userIdInMdc);
        assertEquals(JwtTestSupport.SESSION_ID, chain.sessionIdInMdc);
    }

    @Test
    void bearerWithExtraWhitespaceIsTrimmed() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Bearer   " + JwtTestSupport.validAccessToken() + "  ");

        filter.doFilter(request, new MockHttpServletResponse(), new CapturingChain());

        assertTrue(authorizedUser(request).isPresent());
    }

    @Test
    void repeatedBearerPrefixIsNotStripped() throws Exception {
        MockHttpServletRequest request =
                requestWithAuth("Bearer Bearer " + JwtTestSupport.validAccessToken());
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(authorizedUser(request).isEmpty());
        assertTrue(chain.invoked);
    }

    @Test
    void invalidTokenProceedsAnonymouslyAndLogsWarning() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Bearer " + JwtTestSupport.tokenSignedWithOtherKey());
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, response, chain);

        assertTrue(authorizedUser(request).isEmpty());
        assertTrue(chain.invoked);
        assertEquals(200, response.getStatus());
        assertNull(chain.userIdInMdc);
        assertEquals(1, verifierLogs.at(Level.WARN).size());
    }

    @Test
    void expiredTokenProceedsAnonymouslyWithoutWarning() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Bearer " + JwtTestSupport.expiredAccessToken());
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(authorizedUser(request).isEmpty());
        assertTrue(chain.invoked);
        assertTrue(verifierLogs.at(Level.WARN).isEmpty());
        assertTrue(verifierLogs.has(Level.TRACE, "access jwt: jwt token was expired"));
    }

    @Test
    void unexpectedVerifierErrorGives500AndStopsChain() throws Exception {
        AccessJwtVerifier failingVerifier = mock(AccessJwtVerifier.class);
        when(failingVerifier.verify(anyString())).thenThrow(new IllegalStateException("boom"));
        AccessTokenFilter failingFilter = new AccessTokenFilter(failingVerifier);

        MockHttpServletRequest request = requestWithAuth("Bearer anything");
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        failingFilter.doFilter(request, response, chain);

        assertEquals(500, response.getStatus());
        assertFalse(chain.invoked);
        assertNull(request.getAttribute(AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY));

        List<ILoggingEvent> errors = filterLogs.at(Level.ERROR);
        assertEquals(1, errors.size());
        assertEquals("AccessTokenFilter error", errors.getFirst().getFormattedMessage());
        assertNotNull(errors.getFirst().getThrowableProxy());
    }

    @Test
    void validSignatureButBrokenPayloadGives500() throws Exception {
        MockHttpServletRequest request =
                requestWithAuth("Bearer " + JwtTestSupport.validSignatureBrokenPayloadToken());
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, response, chain);

        assertEquals(500, response.getStatus());
        assertFalse(chain.invoked);
        assertEquals(1, filterLogs.at(Level.ERROR).size());
    }

    @Test
    void mdcIsClearedAfterSuccessfulRequest() throws Exception {
        MockHttpServletRequest request = requestWithAuth("Bearer " + JwtTestSupport.validAccessToken());

        filter.doFilter(request, new MockHttpServletResponse(), new CapturingChain());

        assertNull(MDC.get(MDCFields.USER_ID_KEY));
        assertNull(MDC.get(MDCFields.SESSION_ID_KEY));
    }

    @Test
    void mdcIsClearedWhenChainThrows() {
        MockHttpServletRequest request = requestWithAuth("Bearer " + JwtTestSupport.validAccessToken());
        FilterChain throwingChain = (_, _) -> {
            throw new ServletException("downstream failure");
        };

        assertThrows(
                ServletException.class,
                () -> filter.doFilter(request, new MockHttpServletResponse(), throwingChain)
        );

        assertNull(MDC.get(MDCFields.USER_ID_KEY));
        assertNull(MDC.get(MDCFields.SESSION_ID_KEY));
    }

    @Test
    void mdcDoesNotLeakIntoNextAnonymousRequestOnSameThread() throws Exception {
        filter.doFilter(
                requestWithAuth("Bearer " + JwtTestSupport.validAccessToken()),
                new MockHttpServletResponse(),
                new CapturingChain()
        );

        CapturingChain secondChain = new CapturingChain();
        filter.doFilter(requestWithAuth(null), new MockHttpServletResponse(), secondChain);

        assertNull(secondChain.userIdInMdc, "userId from the previous request leaked");
        assertNull(secondChain.sessionIdInMdc, "sessionId from the previous request leaked");
    }
}
