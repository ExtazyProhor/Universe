package ru.prohor.universe.scarif.services.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegisterIpRateLimiter extends IpRateLimiter {
    public RegisterIpRateLimiter(
            @Value("${universe.scarif.rateLimiterCacheTtlMinutes}") int cacheTtlMinutes,
            @Value("${universe.scarif.rateLimiterCacheMaxSize}") int cacheMaxSize,
            @Value("${universe.scarif.rateLimiterMaxRegisterAttempts}") int rateLimiterMaxRegisterAttempts
    ) {
        super("Register", rateLimiterMaxRegisterAttempts, cacheTtlMinutes, cacheMaxSize);
    }
}
