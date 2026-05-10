package com.coderych.commons.cache.support;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheKeyBuilderTests {

    private CacheProperties cacheProperties;

    @BeforeEach
    void setUp() {
        cacheProperties = new CacheProperties();
        cacheProperties.setKeyPrefix("app:");
        cacheProperties.getRedis().setKeyPrefix("cache:");
        cacheProperties.getLock().setKeyPrefix("lock:");
        cacheProperties.getQueue().setConsumerGroupPrefix("queue:");
        CacheKeyBuilder.init(cacheProperties);
    }

    @Test
    void initShouldThrowWhenPropertiesIsNull() {
        assertThrows(IllegalArgumentException.class, () -> CacheKeyBuilder.init(null));
    }

    @Test
    void buildRedisKeyShouldCombinePrefixAndKey() {
        String result = CacheKeyBuilder.buildRedisKey("user:1");
        assertEquals("app:cache:user:1", result);
    }

    @Test
    void buildRedisKeyShouldWorkWithEmptyKey() {
        String result = CacheKeyBuilder.buildRedisKey("");
        assertEquals("app:cache:", result);
    }

    @Test
    void buildLockKeyShouldCombinePrefixNameAndBusinessKey() {
        String result = CacheKeyBuilder.buildLockKey("order", "123");
        assertEquals("app:lock:order:123", result);
    }

    @Test
    void buildLockKeyShouldUseNameOnlyWhenBusinessKeyIsBlank() {
        String result = CacheKeyBuilder.buildLockKey("order", "");
        assertEquals("app:lock:order", result);
    }

    @Test
    void buildLockKeyShouldUseNameOnlyWhenBusinessKeyIsNull() {
        String result = CacheKeyBuilder.buildLockKey("order", null);
        assertEquals("app:lock:order", result);
    }

    @Test
    void buildLockKeyShouldUseNameOnlyWhenBusinessKeyIsWhitespace() {
        String result = CacheKeyBuilder.buildLockKey("order", "   ");
        assertEquals("app:lock:order", result);
    }

    @Test
    void buildQueueKeyShouldCombinePrefixAndQueueName() {
        String result = CacheKeyBuilder.buildQueueKey("email");
        assertEquals("app:queue:email", result);
    }

    @Test
    void buildShouldCombineSubPrefixAndKey() {
        String result = CacheKeyBuilder.build("custom:", "mykey");
        assertEquals("app:custom:mykey", result);
    }

    @Test
    void buildShouldSkipBlankSubPrefix() {
        String result = CacheKeyBuilder.build("", "mykey");
        assertEquals("app:mykey", result);
    }

    @Test
    void buildShouldSkipNullSubPrefix() {
        String result = CacheKeyBuilder.build(null, "mykey");
        assertEquals("app:mykey", result);
    }

    @Test
    void buildShouldSkipBlankKeyPrefix() {
        cacheProperties.setKeyPrefix("");
        CacheKeyBuilder.init(cacheProperties);
        String result = CacheKeyBuilder.buildRedisKey("mykey");
        assertEquals("cache:mykey", result);
    }

    @Test
    void buildShouldSkipNullKeyPrefix() {
        cacheProperties.setKeyPrefix(null);
        CacheKeyBuilder.init(cacheProperties);
        String result = CacheKeyBuilder.buildRedisKey("mykey");
        assertEquals("cache:mykey", result);
    }

    @Test
    void initShouldReinitializeWithNewProperties() {
        CacheProperties newProps = new CacheProperties();
        newProps.setKeyPrefix("new:");
        newProps.getRedis().setKeyPrefix("rc:");
        CacheKeyBuilder.init(newProps);
        String result = CacheKeyBuilder.buildRedisKey("k");
        assertEquals("new:rc:k", result);
    }
}
