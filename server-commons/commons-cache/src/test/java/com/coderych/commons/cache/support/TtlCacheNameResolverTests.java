package com.coderych.commons.cache.support;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.core.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TtlCacheNameResolverTests {

    private CacheProperties cacheProperties;

    @BeforeEach
    void setUp() {
        cacheProperties = new CacheProperties();
        cacheProperties.getRedis().setTtlSeparator("#");
        cacheProperties.getRedis().setDefaultTtl(Duration.ofMinutes(30));
        TtlCacheNameResolver.init(cacheProperties);
    }

    @Test
    void initShouldThrowWhenPropertiesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> TtlCacheNameResolver.init(null));
    }

    @Test
    void resolveShouldReturnDefaultTtlWhenNoSeparator() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldReturnDefaultTtlWhenCacheNameIsBlank() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("");
        assertEquals("", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldReturnDefaultTtlWhenCacheNameIsNull() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve(null);
        assertNull(result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldParseMinutesTtl() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#30m");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldParseSecondsTtl() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#45s");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofSeconds(45), result.ttl());
    }

    @Test
    void resolveShouldParseMillisTtl() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#500ms");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofMillis(500), result.ttl());
    }

    @Test
    void resolveShouldParseHoursTtl() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#2h");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofHours(2), result.ttl());
    }

    @Test
    void resolveShouldParseDaysTtl() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#1d");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofDays(1), result.ttl());
    }

    @Test
    void resolveShouldBeCaseInsensitive() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#30M");
        assertEquals("myCache", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldHandleMultipleSeparatorsUsingLast() {
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("my#Cache#10m");
        assertEquals("my#Cache", result.cacheName());
        assertEquals(Duration.ofMinutes(10), result.ttl());
    }

    @Test
    void resolveShouldThrowWhenSeparatorAtStart() {
        assertThrows(BadRequestException.class, () -> TtlCacheNameResolver.resolve("#30m"));
    }

    @Test
    void resolveShouldThrowWhenSeparatorAtEnd() {
        assertThrows(BadRequestException.class, () -> TtlCacheNameResolver.resolve("myCache#"));
    }

    @Test
    void resolveShouldThrowWhenTtlValueIsInvalid() {
        assertThrows(BadRequestException.class, () -> TtlCacheNameResolver.resolve("myCache#abc"));
    }

    @Test
    void resolveShouldThrowWhenTtlNumberIsInvalid() {
        assertThrows(BadRequestException.class, () -> TtlCacheNameResolver.resolve("myCache#xyzm"));
    }

    @Test
    void resolveShouldReturnDefaultTtlWhenSeparatorIsBlank() {
        cacheProperties.getRedis().setTtlSeparator("");
        TtlCacheNameResolver.init(cacheProperties);
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#30m");
        assertEquals("myCache#30m", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolveShouldReturnDefaultTtlWhenSeparatorIsNull() {
        cacheProperties.getRedis().setTtlSeparator(null);
        TtlCacheNameResolver.init(cacheProperties);
        TtlCacheNameResolver.ResolvedCacheName result = TtlCacheNameResolver.resolve("myCache#30m");
        assertEquals("myCache#30m", result.cacheName());
        assertEquals(Duration.ofMinutes(30), result.ttl());
    }

    @Test
    void resolvedCacheNameRecordShouldHaveCorrectValues() {
        Duration ttl = Duration.ofMinutes(15);
        TtlCacheNameResolver.ResolvedCacheName record = new TtlCacheNameResolver.ResolvedCacheName("name", ttl);
        assertEquals("name", record.cacheName());
        assertEquals(ttl, record.ttl());
    }
}
