package ru.prohor.universe.jocasta.scarifJwt;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.springweb.CookieUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AccessTokenFilter extends OncePerRequestFilter {
    public static final int ACCESS_TOKEN_FILTER_ORDER = 5;

    private final String accessTokenCookieName;
    private final JwtVerifier jwtVerifier;

    public AccessTokenFilter(String accessTokenCookieName, JwtVerifier jwtVerifier) {
        this.accessTokenCookieName = accessTokenCookieName;
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        // TODO log cookies ONLY NAMES
        System.out.println(
                Arrays.stream(Opt.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                        .map(Cookie::getName)
                        .collect(Collectors.joining(", ", "cookies: {", "}"))
        );

        Opt<AuthorizedUser> authorizedUser = CookieUtil
                .getCookieValue(request, accessTokenCookieName)
                .flatMapO(jwtVerifier::verify);
        request.setAttribute(
                AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY,
                authorizedUser
        );
        filterChain.doFilter(request, response);
    }
}
