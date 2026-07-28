package com.csj.inventory.service;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    <T> Optional<T> get(String key, Class<T> type, String entity);

    void put(String key, Object value, Duration ttl, String entity);

    void evict(String key, String entity);

    void evictByPrefix(String prefix, String entity);
}
