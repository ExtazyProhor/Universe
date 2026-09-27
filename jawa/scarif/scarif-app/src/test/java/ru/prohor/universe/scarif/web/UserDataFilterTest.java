package ru.prohor.universe.scarif.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.scarif.services.refresh.RefreshJwtVerifier;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserDataFilterTest {
    private static final String COOKIE_NAME = "refresh_token";

    private final RefreshJwtVerifier refreshJwtVerifier = mock(RefreshJwtVerifier.class);
    private final UserDataFilter filter = new UserDataFilter(COOKIE_NAME, refreshJwtVerifier);

    private static final class CapturingChain implements FilterChain {
        boolean invoked;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            invoked = true;
        }
    }

    private static UserData userDataAttribute(MockHttpServletRequest request) {
        Object attr = request.getAttribute(UserData.USER_DATA_ATTRIBUTE_KEY);
        assertNotNull(attr, "UserData всегда должна быть выставлена как атрибут");
        return (UserData) attr;
    }

    @SuppressWarnings("unchecked")
    private static Opt<RefreshToken> refreshTokenAttribute(MockHttpServletRequest request) {
        Object attr = request.getAttribute(RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY);
        assertNotNull(attr, "атрибут должен быть выставлен даже когда токена нет");
        return (Opt<RefreshToken>) attr;
    }

    @Test
    void usesRemoteAddrWhenNoForwardedForHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.5");
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(chain.invoked);
        assertEquals("10.0.0.5", userDataAttribute(request).ip());
    }

    @Test
    void usesForwardedForHeaderWhenPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.5");
        request.addHeader(UserDataFilter.IP_HEADER, "203.0.113.7");
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertEquals("203.0.113.7", userDataAttribute(request).ip());
    }

    @Test
    void userAgentIsEmptyWhenHeaderAbsent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(userDataAttribute(request).userAgent().isEmpty());
    }

    @Test
    void userAgentIsCapturedWhenPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserDataFilter.USER_AGENT_HEADER, "test-agent/1.0");
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertEquals(Opt.of("test-agent/1.0"), userDataAttribute(request).userAgent());
    }

    @Test
    void noRefreshTokenCookieGivesEmptyAttributeAndProceeds() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(chain.invoked);
        assertTrue(refreshTokenAttribute(request).isEmpty());
        verifyNoInteractions(refreshJwtVerifier);
    }

    @Test
    void validRefreshTokenCookieIsVerifiedAndSetAsAttribute() throws Exception {
        RefreshToken refreshToken = new RefreshToken(ObjectId.get(), ObjectId.get(), ObjectId.get());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(COOKIE_NAME, "cookie-value"));
        when(refreshJwtVerifier.verify("cookie-value")).thenReturn(Opt.of(refreshToken));
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertEquals(Opt.of(refreshToken), refreshTokenAttribute(request));
    }

    @Test
    void invalidRefreshTokenCookieGivesEmptyAttribute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(COOKIE_NAME, "bad-cookie"));
        when(refreshJwtVerifier.verify("bad-cookie")).thenReturn(Opt.empty());
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(refreshTokenAttribute(request).isEmpty());
    }

    @Test
    void unrelatedCookiesAreIgnored() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("some_other_cookie", "value"));
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertTrue(refreshTokenAttribute(request).isEmpty());
        verifyNoInteractions(refreshJwtVerifier);
    }

    @Test
    void unexpectedErrorGives500AndStopsChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(COOKIE_NAME, "cookie-value"));
        when(refreshJwtVerifier.verify("cookie-value")).thenThrow(new RuntimeException("boom"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        CapturingChain chain = new CapturingChain();

        filter.doFilter(request, response, chain);

        assertEquals(500, response.getStatus());
        assertFalse(chain.invoked);
    }
}
