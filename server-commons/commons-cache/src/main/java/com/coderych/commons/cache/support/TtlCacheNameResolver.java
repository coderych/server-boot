package com.coderych.commons.cache.support;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.core.exception.BadRequestException;
import com.coderych.commons.core.util.STR;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Locale;

/**
 * 缓存名称解析器，从缓存名中提取自定义 TTL 后缀。
 * <p>格式：{@code cacheName#30m}，支持 ms/s/m/h/d 单位。未指定则使用全局默认 TTL。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TtlCacheNameResolver {

    private static volatile CacheProperties cacheProperties;

    public static void init(CacheProperties cacheProperties) {
        if (cacheProperties == null) {
            throw new IllegalArgumentException("CacheProperties must not be null");
        }
        TtlCacheNameResolver.cacheProperties = cacheProperties;
    }

    public static ResolvedCacheName resolve(String cacheName) {
        String separator = cacheProperties.getRedis().getTtlSeparator();
        if (STR.isBlank(separator) || STR.isBlank(cacheName) || !cacheName.contains(separator)) {
            return new ResolvedCacheName(cacheName, cacheProperties.getRedis().getDefaultTtl());
        }
        int index = cacheName.lastIndexOf(separator);
        if (index <= 0 || index == cacheName.length() - separator.length()) {
            throw new BadRequestException("Invalid cache name TTL suffix: " + cacheName);
        }
        String logicalName = cacheName.substring(0, index);
        String ttlValue = cacheName.substring(index + separator.length());
        return new ResolvedCacheName(logicalName, parseDuration(ttlValue, cacheName));
    }

    private static Duration parseDuration(String ttlValue, String cacheName) {
        String normalized = ttlValue.toLowerCase(Locale.ROOT);
        try {
            if (normalized.endsWith("ms")) {
                return Duration.ofMillis(Long.parseLong(normalized.substring(0, normalized.length() - 2)));
            }
            if (normalized.endsWith("s")) {
                return Duration.ofSeconds(Long.parseLong(normalized.substring(0, normalized.length() - 1)));
            }
            if (normalized.endsWith("m")) {
                return Duration.ofMinutes(Long.parseLong(normalized.substring(0, normalized.length() - 1)));
            }
            if (normalized.endsWith("h")) {
                return Duration.ofHours(Long.parseLong(normalized.substring(0, normalized.length() - 1)));
            }
            if (normalized.endsWith("d")) {
                return Duration.ofDays(Long.parseLong(normalized.substring(0, normalized.length() - 1)));
            }
        } catch (NumberFormatException exception) {
            throw new BadRequestException("Invalid cache name TTL suffix: " + cacheName, exception);
        }
        throw new BadRequestException("Invalid cache name TTL suffix: " + cacheName);
    }

    public record ResolvedCacheName(String cacheName, Duration ttl) {
    }
}
