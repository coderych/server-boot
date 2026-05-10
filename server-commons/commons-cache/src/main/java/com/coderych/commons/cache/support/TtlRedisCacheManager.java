package com.coderych.commons.cache.support;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持每个缓存独立 TTL 的 Redis CacheManager。
 * <p>通过 {@link TtlCacheNameResolver} 解析缓存名中的 TTL 后缀，实现差异化过期策略。</p>
 *
 * @author YCH
 */
public class TtlRedisCacheManager implements CacheManager {

    private final RedisCacheWriter cacheWriter;

    private final RedisCacheConfiguration defaultConfiguration;

    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    public TtlRedisCacheManager(RedisConnectionFactory connectionFactory,
                                RedisCacheConfiguration defaultConfiguration) {
        this.cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
        this.defaultConfiguration = defaultConfiguration;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, this::createCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(caches.keySet());
    }

    private Cache createCache(String name) {
        TtlCacheNameResolver.ResolvedCacheName resolvedCacheName = TtlCacheNameResolver.resolve(name);
        RedisCacheConfiguration configuration = defaultConfiguration.entryTtl(resolvedCacheName.ttl());
        return new ManagedRedisCache(resolvedCacheName.cacheName(), cacheWriter, configuration);
    }

    private static final class ManagedRedisCache extends RedisCache {
        private ManagedRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
            super(name, cacheWriter, cacheConfig);
        }
    }
}
