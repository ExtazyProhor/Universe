package ru.prohor.universe.scarif.jwt;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.io.IOException;

@Component
@Order(-100)
public class AccessTokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AccessTokenFilter.class);

    private final AccessJwtVerifier accessJwtVerifier;

    public AccessTokenFilter(AccessJwtVerifier accessJwtVerifier) {
        this.accessJwtVerifier = accessJwtVerifier;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Opt<AuthorizedUser> authorizedUser = extractAuthorizedUser(request);
            authorizedUser.ifPresent(user -> MDC.put(MDCFields.USER_ID_KEY, user.objectId()));
            request.setAttribute(AuthorizedUser.AUTHORIZED_USER_ATTRIBUTE_KEY, authorizedUser);
        } catch (Exception e) {
            log.error("AccessTokenFilter error", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Opt<AuthorizedUser> extractAuthorizedUser(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            log.trace("Auth header not present");
            return Opt.empty();
        }
        if (!header.startsWith("Bearer ")) {
            log.warn("Illegal structure of auth header");
            return Opt.empty();
        }
        String token = header.replace("Bearer ", "").trim();
        return accessJwtVerifier.verify(token);
    }
}
