package ru.prohor.universe.scarif.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.security.rsa.PublicKeyProvider;
import ru.prohor.universe.scarif.jwt.AccessJwtVerifier;
import ru.prohor.universe.scarif.jwt.AccessTokenFilter;
import ru.prohor.universe.scarif.jwt.MDCFields;
import ru.prohor.universe.scarif.services.refresh.RefreshJwtVerifier;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterOrderingTest {
    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String validAccessToken(KeyPair keyPair) {
        return JWT.create()
                .withSubject("42")
                .withClaim("uid", UUID.randomUUID().toString())
                .withClaim("oid", "obj-1")
                .withClaim("sid", "session-1")
                .withJWTId("jti-1")
                .withExpiresAt(Instant.now().plusSeconds(300))
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    private static AccessTokenFilter accessTokenFilter(KeyPair keyPair) {
        PublicKeyProvider keyProvider = () -> (RSAPublicKey) keyPair.getPublic();
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new AccessTokenFilter(new AccessJwtVerifier(keyProvider, objectMapper));
    }

    private static UserDataFilter userDataFilterReturningNoRefreshToken() {
        RefreshJwtVerifier refreshJwtVerifier = mock(RefreshJwtVerifier.class);
        when(refreshJwtVerifier.verify(anyString())).thenReturn(Opt.empty());
        return new UserDataFilter("refresh_token", refreshJwtVerifier);
    }

    private static FilterChain chainThrough(
            UserDataFilter userDataFilter,
            AccessTokenFilter accessTokenFilter,
            FilterChain terminal
    ) {
        FilterChain viaAccessToken = (ServletRequest req, ServletResponse res) ->
                accessTokenFilter.doFilter((HttpServletRequest) req, (HttpServletResponse) res, terminal);
        return (ServletRequest req, ServletResponse res) ->
                userDataFilter.doFilter((HttpServletRequest) req, (HttpServletResponse) res, viaAccessToken);
    }

    @Test
    void mdcIsFullyClearedByOutermostFilterAfterWholeChain() throws Exception {
        KeyPair keyPair = generateKeyPair();
        AccessTokenFilter accessTokenFilter = accessTokenFilter(keyPair);
        UserDataFilter userDataFilter = userDataFilterReturningNoRefreshToken();
        LogRequestContextFilter logRequestContextFilter = new LogRequestContextFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validAccessToken(keyPair));
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String, String> mdcDuringChain = new HashMap<>();
        FilterChain terminal = (_, _) -> {
            mdcDuringChain.put("userId", MDC.get(MDCFields.USER_ID_KEY));
            mdcDuringChain.put("requestId", MDC.get(MDCFields.REQUEST_ID_KEY));
        };

        logRequestContextFilter.doFilter(request, response, chainThrough(userDataFilter, accessTokenFilter, terminal));

        assertNotNull(mdcDuringChain.get("userId"), "AccessTokenFilter должен был положить userId в MDC");
        assertNotNull(mdcDuringChain.get("requestId"), "LogRequestContextFilter должен был положить requestId в MDC");

        assertNull(MDC.get(MDCFields.USER_ID_KEY));
        assertNull(MDC.get(MDCFields.REQUEST_ID_KEY));
        assertNull(MDC.get(MDCFields.METHOD_KEY));
        assertNull(MDC.get(MDCFields.REQUEST_URL_KEY));
    }

    @Test
    void mdcDoesNotLeakBetweenRequestsOnSameThreadThroughRealFilterChain() throws Exception {
        KeyPair keyPair = generateKeyPair();
        AccessTokenFilter accessTokenFilter = accessTokenFilter(keyPair);
        UserDataFilter userDataFilter = userDataFilterReturningNoRefreshToken();
        LogRequestContextFilter logRequestContextFilter = new LogRequestContextFilter();

        MockHttpServletRequest authenticated = new MockHttpServletRequest("GET", "/api/test");
        authenticated.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validAccessToken(keyPair));
        FilterChain terminalNoop = (_, _) -> {};
        logRequestContextFilter.doFilter(
                authenticated, new MockHttpServletResponse(),
                chainThrough(userDataFilter, accessTokenFilter, terminalNoop)
        );

        Map<String, String> mdcDuringSecondRequest = new HashMap<>();
        FilterChain terminalCapturing = (_, _) -> mdcDuringSecondRequest.put("userId", MDC.get(MDCFields.USER_ID_KEY));
        MockHttpServletRequest anonymous = new MockHttpServletRequest("GET", "/api/test");
        logRequestContextFilter.doFilter(
                anonymous, new MockHttpServletResponse(),
                chainThrough(userDataFilter, accessTokenFilter, terminalCapturing)
        );

        assertNull(
                mdcDuringSecondRequest.get("userId"),
                "userId из первого (аутентифицированного) запроса не должен был утечь во второй"
        );
    }
}
