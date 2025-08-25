package ru.prohor.universe.scarif.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {
    // TODO logger сюда и в остальной код
    private static final String LOGIN_PREFIX = "login:";
    private static final String REGISTER_PREFIX = "register:";

    private final Cache<String, AtomicInteger> attemptCache;
    private final int rateLimiterMaxLoginAttempts;
    private final int rateLimiterMaxRegisterAttempts;

    public RateLimitService(
            @Value("${universe.scarif.rateLimiterCacheTtlMinutes}") int cacheTtlMinutes,
            @Value("${universe.scarif.rateLimiterCacheMaxSize}") int cacheMaxSize,
            @Value("${universe.scarif.rateLimiterMaxLoginAttempts}") int rateLimiterMaxLoginAttempts,
            @Value("${universe.scarif.rateLimiterMaxRegisterAttempts}") int rateLimiterMaxRegisterAttempts
    ) {
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(cacheTtlMinutes))
                .maximumSize(cacheMaxSize)
                .build();
        this.rateLimiterMaxLoginAttempts = rateLimiterMaxLoginAttempts;
        this.rateLimiterMaxRegisterAttempts = rateLimiterMaxRegisterAttempts;
    }

    private boolean isAllowed(String key, int maxAttempts) {
        AtomicInteger attempts = attemptCache.asMap().computeIfAbsent(
                key,
                k -> new AtomicInteger(0)
        );
        return attempts.incrementAndGet() <= maxAttempts;
    }

    private void reset(String key) {
        attemptCache.invalidate(key);
    }

    public boolean isLoginAllowed(String ip) {
        return isAllowed(LOGIN_PREFIX + ip, rateLimiterMaxLoginAttempts);
    }

    public boolean isRegisterAllowed(String ip) {
        return isAllowed(REGISTER_PREFIX + ip, rateLimiterMaxRegisterAttempts);
    }

    public void loginReset(String ip) {
        reset(LOGIN_PREFIX + ip);
    }

    public void registerReset(String ip) {
        reset(REGISTER_PREFIX + ip);
    }

    /* TODO Админка

    public CacheStats getStats() {
        return attemptCache.stats();
    }

    // Метод для получения размера кеша
    public long getCacheSize() {
        return attemptCache.estimatedSize();
    }

    // Очистка кеша (для админки)
    public void clearAll() {
        attemptCache.invalidateAll();
        logger.info("Rate limit cache cleared");
    }

    */
}
