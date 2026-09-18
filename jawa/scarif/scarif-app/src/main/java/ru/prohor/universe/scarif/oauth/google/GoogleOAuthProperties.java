package ru.prohor.universe.scarif.oauth.google;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class GoogleOAuthProperties {
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private final String redirectUrl;
    private final String tokenExchangeUrl;
    private final String jwkSetUrl;
    private final Duration jwkSetCacheTtl;
    private final String issuer;

    public GoogleOAuthProperties(
            @Value("${universe.scarif.oauth.google.client-id}")
            String clientId,
            @Value("${universe.scarif.oauth.google.client-secret}")
            String clientSecret,
            @Value("${universe.scarif.oauth.google.auth-url}")
            String authUrl,
            @Value("${universe.scarif.oauth.google.redirect-url}")
            String redirectUrl,
            @Value("${universe.scarif.oauth.google.token-exchange-url}")
            String tokenExchangeUrl,
            @Value("${universe.scarif.oauth.google.jwk-set-url}")
            String jwkSetUrl,
            @Value("${universe.scarif.oauth.google.jwk-set-cache-ttl}")
            Duration jwkSetCacheTtl,
            @Value("${universe.scarif.oauth.google.issuer}")
            String issuer
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
        this.redirectUrl = redirectUrl;
        this.tokenExchangeUrl = tokenExchangeUrl;
        this.jwkSetUrl = jwkSetUrl;
        this.jwkSetCacheTtl = jwkSetCacheTtl;
        this.issuer = issuer;
    }

    public String clientId() {
        return clientId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public String authUrl() {
        return authUrl;
    }

    public String redirectUrl() {
        return redirectUrl;
    }

    public String tokenExchangeUrl() {
        return tokenExchangeUrl;
    }

    public String jwkSetUrl() {
        return jwkSetUrl;
    }

    public Duration jwkSetCacheTtl() {
        return jwkSetCacheTtl;
    }

    public String issuer() {
        return issuer;
    }
}
