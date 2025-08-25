package ru.prohor.universe.scarif.services;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Opt;

@Service
public class CookieProvider {
    private static final String SAME_SITE_NONE = "None";
    private static final String SCARIF_API_PATH = "/api/auth";
    private static final String ROOT_PATH = "/";

    private final String refreshTokenCookieName;
    private final long refreshTokenTtlDays;
    private final String accessTokenCookieName;
    private final long accessTokenTtlMinutes;
    private final Opt<String> universeDomain;

    public CookieProvider(
            @Value("${universe.scarif.refreshTokenCookieName}") String refreshTokenCookieName,
            @Value("${universe.scarif.refreshTokenTtlDays}") long refreshTokenTtlDays,
            @Value("${universe.scarif.accessTokenCookieName}") String accessTokenCookieName,
            @Value("${universe.scarif.accessTokenTtlMinutes}") long accessTokenTtlMinutes,
            @Value("${universe.domain}") String universeDomain
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
        this.accessTokenCookieName = accessTokenCookieName;
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        this.universeDomain = Opt.of(universeDomain);
    }

    public String createRefreshCookie(String token) {
        return cookie(
                refreshTokenCookieName,
                token,
                SCARIF_API_PATH,
                Opt.empty(),
                Duration.standardDays(refreshTokenTtlDays).getStandardSeconds()
        );
    }

    public String clearRefreshCookie() {
        return cookie(
                refreshTokenCookieName,
                "",
                SCARIF_API_PATH,
                Opt.empty(),
                0
        );
    }

    public String createAccessCookie(String token) {
        return cookie(
                accessTokenCookieName,
                token,
                ROOT_PATH,
                universeDomain,
                Duration.standardMinutes(accessTokenTtlMinutes).getStandardSeconds()
        );
    }

    public String clearAccessCookie() {
        return cookie(
                accessTokenCookieName,
                "",
                ROOT_PATH,
                universeDomain,
                0
        );
    }

    private String cookie(String name, String value, String path, Opt<String> domain, long maxAge) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite(SAME_SITE_NONE)
                .path(path)
                .maxAge(maxAge);
        if (domain.isPresent())
            cookieBuilder = cookieBuilder.domain(domain.get());
        return cookieBuilder
                .build()
                .toString();
    }
}
