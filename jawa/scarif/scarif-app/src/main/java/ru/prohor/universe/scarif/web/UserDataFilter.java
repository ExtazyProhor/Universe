package ru.prohor.universe.scarif.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.springweb.CookieUtil;
import ru.prohor.universe.scarif.services.refresh.RefreshJwtVerifier;
import ru.prohor.universe.scarif.services.refresh.RefreshToken;

import java.io.IOException;

@Component
public class UserDataFilter extends OncePerRequestFilter { // TODO CSRF security
    public static final String IP_HEADER = "X-Forwarded-For";
    public static final String USER_AGENT_HEADER = "User-Agent";
    private static final Logger log = LoggerFactory.getLogger(UserDataFilter.class);

    private final String refreshTokenCookieName;
    private final RefreshJwtVerifier refreshJwtVerifier;

    public UserDataFilter(
            @Value("${universe.scarif.refresh-token.cookie-name}") String refreshTokenCookieName,
            RefreshJwtVerifier refreshJwtVerifier
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshJwtVerifier = refreshJwtVerifier;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String ip = Opt.ofNullable(request.getHeader(IP_HEADER)).orElseGet(request::getRemoteAddr);
        Opt<String> userAgent = Opt.ofNullable(request.getHeader(USER_AGENT_HEADER));

        UserData userData = new UserData(ip, userAgent);
        log.trace("parse userData from request: {}", userData);
        request.setAttribute(UserData.USER_DATA_ATTRIBUTE_KEY, userData);

        Opt<RefreshToken> refreshToken = CookieUtil.getCookieValue(request, refreshTokenCookieName)
                .flatMapO(refreshJwtVerifier::verify);
        request.setAttribute(RefreshToken.REFRESH_TOKEN_ATTRIBUTE_KEY, refreshToken);

        filterChain.doFilter(request, response);
    }
}
