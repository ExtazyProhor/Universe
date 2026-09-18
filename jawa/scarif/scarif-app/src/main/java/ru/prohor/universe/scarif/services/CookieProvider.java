package ru.prohor.universe.scarif.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieProvider {
    private final String refreshTokenCookieName;
    private final long refreshTokenTtlSeconds;
    private final String sameSite;
    private final boolean secure;

    public CookieProvider(
            @Value("${universe.scarif.refresh-token.cookie-name}") String refreshTokenCookieName,
            @Value("${universe.scarif.refresh-token.ttl}") Duration refreshTokenTtl,
            @Value("${universe.scarif.refresh-token.same-site}") String sameSite,
            @Value("${universe.scarif.refresh-token.secure}") boolean secure
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.refreshTokenTtlSeconds = refreshTokenTtl.getSeconds();
        this.sameSite = sameSite;
        this.secure = secure;
    }

    public String createRefreshCookie(String token) {
        return cookie(
                refreshTokenCookieName,
                token,
                refreshTokenTtlSeconds
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
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
