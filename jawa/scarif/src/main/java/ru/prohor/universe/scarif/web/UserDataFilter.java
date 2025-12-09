package ru.prohor.universe.scarif.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.springweb.CookieUtil;
import ru.prohor.universe.scarif.services.refresh.ParsedUserToken;
import ru.prohor.universe.scarif.services.refresh.RefreshTokenService;
import ru.prohor.universe.scarif.services.refresh.UserTokenParsingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class UserDataFilter extends OncePerRequestFilter { // TODO CSRF security
    private static final Logger log = LoggerFactory.getLogger(UserDataFilter.class);

    private final String refreshTokenCookieName;
    private final RefreshTokenService refreshTokenService;

    public UserDataFilter(
            @Value("${universe.scarif.refreshTokenCookieName}") String refreshTokenCookieName,
            RefreshTokenService refreshTokenService
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String ip = Opt.ofNullable(request.getHeader(Requests.IP_HEADER)).orElseGet(request::getRemoteAddr);
        Opt<String> userAgent = Opt.ofNullable(request.getHeader(Requests.USER_AGENT_HEADER));

        UserData userData = new UserData(ip, userAgent);
        log.trace("parse userData from request: {}", userData);
        request.setAttribute(Requests.USER_DATA_ATTRIBUTE_KEY, userData);

        Opt<String> optUserToken = CookieUtil.getCookieValue(request, refreshTokenCookieName);
        Opt<ParsedUserToken> parsedUserToken;
        if (optUserToken.isPresent()) {
            String userToken = optUserToken.get();
            try {
                parsedUserToken = Opt.of(refreshTokenService.parseUserToken(userToken));
            } catch (UserTokenParsingException e) {
                log.warn("[IS] error when parsing userToken (refresh cookie)", e);
                unauthorized(response);
                return;
            }
        } else {
            parsedUserToken = Opt.empty();
        }

        request.setAttribute(
                Requests.REFRESH_TOKEN_ATTRIBUTE_KEY,
                parsedUserToken
        );
        filterChain.doFilter(request, response);
    }

    public void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
    }
}
