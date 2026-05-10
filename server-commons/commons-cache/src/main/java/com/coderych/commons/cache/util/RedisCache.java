package com.coderych.commons.cache.util;

import com.coderych.commons.cache.support.CacheKeyBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 缓存操作工具类，封装常用的 KV 操作。
 * <p>key 会自动通过 {@link CacheKeyBuilder} 添加前缀。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisCache {

    @Getter
    private static volatile RedisTemplate<String, Object> redisTemplate;

    public static void init(RedisTemplate<String, Object> redisTemplate) {
        if (redisTemplate == null) {
            throw new IllegalArgumentException("RedisTemplate must not be null");
        }
        RedisCache.redisTemplate = redisTemplate;
    }

    public static Object get(String key) {
        return redisTemplate.opsForValue().get(buildKey(key));
    }

    public static <T> T get(String key, Class<T> targetType) {
        Object value = get(key);
        return value == null ? null : targetType.cast(value);
    }

    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(buildKey(key), value);
    }

    public static void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(buildKey(key), value, ttl);
    }

    public static boolean setIfAbsent(String key, Object value, Duration ttl) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(buildKey(key), value, ttl);
        return Boolean.TRUE.equals(success);
    }

    public static boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(buildKey(key)));
    }

    public static long delete(Collection<String> keys) {
        Set<String> actualKeys = keys.stream().map(RedisCache::buildKey).collect(Collectors.toSet());
        Long deleted = redisTemplate.delete(actualKeys);
        return deleted == null ? 0 : deleted;
    }

    public static boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(key)));
    }

    public static boolean expire(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.expire(buildKey(key), ttl));
    }

    public static long increment(String key, long delta) {
        Long value = redisTemplate.opsForValue().increment(buildKey(key), delta);
        return value == null ? 0L : value;
    }

    public static long decrement(String key, long delta) {
        Long value = redisTemplate.opsForValue().increment(buildKey(key), -delta);
        return value == null ? 0L : value;
    }

    public static long getExpire(String key, TimeUnit timeUnit) {
        Long value = redisTemplate.getExpire(buildKey(key), timeUnit);
        return value == null ? -1L : value;
    }

    private static String buildKey(String key) {
        return CacheKeyBuilder.buildRedisKey(Objects.requireNonNull(key, "key"));
    }
}
