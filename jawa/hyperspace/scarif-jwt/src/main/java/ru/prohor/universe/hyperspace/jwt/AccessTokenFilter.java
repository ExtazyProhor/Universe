package ru.prohor.universe.hyperspace.jwt;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.io.IOException;

public class AccessTokenFilter extends OncePerRequestFilter {
    public static final int ACCESS_TOKEN_FILTER_ORDER = 5;

    private final JwtVerifier jwtVerifier;

    public AccessTokenFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        Opt<AuthorizedUser> authorizedUser = extractAuthorizedUser(request);
        request.setAttribute(AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY, authorizedUser);
        filterChain.doFilter(request, response);
    }

    private Opt<AuthorizedUser> extractAuthorizedUser(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            log(request, "Auth header not present"); // TODO log
            return Opt.empty();
        }
        if (!header.startsWith("Bearer ")) {
            log(request, "Illegal structure of auth header"); // TODO log
            return Opt.empty();
        }
        String token = header.replace("Bearer ", "").trim();
        Opt<AuthorizedUser> user = jwtVerifier.verify(token);
        if (user.isEmpty()) {
            log(request, "jwt verification failed"); // TODO log
        } else {
            log(request, "jwt verified"); // TODO log
        }
        return user;
    }

    private void log(HttpServletRequest request, String s) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        System.out.println(path + ": " + s);
    }
}
