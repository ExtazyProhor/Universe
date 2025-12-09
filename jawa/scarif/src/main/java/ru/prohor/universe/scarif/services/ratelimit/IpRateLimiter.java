package ru.prohor.universe.scarif.services.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class IpRateLimiter {
    private static final Logger log = LoggerFactory.getLogger(IpRateLimiter.class);

    private final Cache<String, AtomicInteger> attemptCache;
    private final String action;
    private final int maxAttempts;

    protected IpRateLimiter(
            String action,
            int maxAttempts,
            int cacheTtlMinutes,
            int cacheMaxSize
    ) {
        this.action = action;
        this.maxAttempts = maxAttempts;
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(cacheTtlMinutes))
                .maximumSize(cacheMaxSize)
                .build();
    }

    public boolean isAllowed(String ip) {
        AtomicInteger attempts = attemptCache.asMap().computeIfAbsent(
                ip,
                k -> new AtomicInteger(0)
        );
        int reachedAttempts = attempts.incrementAndGet();
        if (reachedAttempts < maxAttempts)
            return true;
        if (reachedAttempts == maxAttempts)
            log.info("{} limit for {} reached", action, ip);
        return false;
    }

    public void reset(String ip) {
        log.debug("{} limit reset for {}", action, ip);
        attemptCache.invalidate(ip);
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
