package com.csj.inventory.service.impl;

import com.csj.inventory.logging.StructuredLogger;
import com.csj.inventory.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StructuredLogger structuredLogger;

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type, String entity) {
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached == null) {
            structuredLogger.logEvent("system", "CACHE_MISS", entity, key, "MISS", null);
            return Optional.empty();
        }
        structuredLogger.logEvent("system", "CACHE_HIT", entity, key, "HIT", null);
        return Optional.of((T) cached);
    }

    @Override
    public void put(String key, Object value, Duration ttl, String entity) {
        redisTemplate.opsForValue().set(key, value, ttl);
        structuredLogger.logEvent("system", "CACHE_PUT", entity, key, "STORED", null);
    }

    @Override
    public void evict(String key, String entity) {
        Boolean deleted = redisTemplate.delete(key);
        structuredLogger.logEvent("system", "CACHE_EVICT", entity, key,
                Boolean.TRUE.equals(deleted) ? "EVICTED" : "NOT_PRESENT", null);
    }

    @Override
    public void evictByPrefix(String prefix, String entity) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            structuredLogger.logEvent("system", "CACHE_EVICT_BULK", entity, prefix, "EVICTED_" + keys.size(), null);
        }
    }
}
