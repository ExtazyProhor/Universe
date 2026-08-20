package ru.prohor.universe.scarif.services.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginIpRateLimiter extends IpRateLimiter {
    public LoginIpRateLimiter(
            @Value("${universe.scarif.rateLimiterCacheTtlMinutes}") int cacheTtlMinutes,
            @Value("${universe.scarif.rateLimiterCacheMaxSize}") int cacheMaxSize,
            @Value("${universe.scarif.rateLimiterMaxLoginAttempts}") int rateLimiterMaxLoginAttempts
    ) {
        super("Login", rateLimiterMaxLoginAttempts, cacheTtlMinutes, cacheMaxSize);
    }
}
