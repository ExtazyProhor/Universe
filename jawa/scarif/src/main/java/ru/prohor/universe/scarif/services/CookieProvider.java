package ru.prohor.universe.scarif.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieProvider {
    private static final String SAME_SITE_NONE = "None";
    private static final String SCARIF_API_PATH = "/api/auth";

    private final String refreshTokenCookieName;
    private final long refreshTokenTtlDays;

    public CookieProvider(
            @Value("${universe.scarif.refreshTokenCookieName}") String refreshTokenCookieName,
            @Value("${universe.scarif.refreshTokenTtlDays}") long refreshTokenTtlDays
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public String createRefreshCookie(String token) {
        return cookie(
                refreshTokenCookieName,
                token,
                Duration.ofDays(refreshTokenTtlDays).getSeconds()
        );
    }

    public String clearRefreshCookie() {
        return cookie(
                refreshTokenCookieName,
                "",
                0
        );
    }

    private String cookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite(SAME_SITE_NONE)
                .path(CookieProvider.SCARIF_API_PATH)
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
