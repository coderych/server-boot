package com.coderych.commons.cache.autoconfigure;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CachePropertiesTests {

    @Test
    void defaultEnabledShouldBeTrue() {
        CacheProperties properties = new CacheProperties();
        assertTrue(properties.isEnabled());
    }

    @Test
    void defaultKeyPrefixShouldBeAppColon() {
        CacheProperties properties = new CacheProperties();
        assertEquals("app:", properties.getKeyPrefix());
    }

    @Test
    void redisDefaultsShouldBeCorrect() {
        CacheProperties.Redis redis = new CacheProperties().getRedis();
        assertEquals("cache:", redis.getKeyPrefix());
        assertEquals(Duration.ofMinutes(30), redis.getDefaultTtl());
        assertEquals("#", redis.getTtlSeparator());
        assertFalse(redis.isCacheNullValues());
    }

    @Test
    void lockDefaultsShouldBeCorrect() {
        CacheProperties.Lock lock = new CacheProperties().getLock();
        assertTrue(lock.isEnabled());
        assertEquals("lock:", lock.getKeyPrefix());
        assertEquals(3, lock.getDefaultWaitTime());
        assertEquals(30, lock.getDefaultLeaseTime());
        assertEquals(TimeUnit.SECONDS, lock.getDefaultTimeUnit());
        assertNotNull(lock.getDefaultMessage());
    }

    @Test
    void initDefaultsShouldBeCorrect() {
        CacheProperties.Init init = new CacheProperties().getInit();
        assertTrue(init.isEnabled());
        assertTrue(init.isAutoRun());
        assertFalse(init.isParallel());
        assertFalse(init.isDefaultFailOnError());
    }

    @Test
    void queueDefaultsShouldBeCorrect() {
        CacheProperties.Queue queue = new CacheProperties().getQueue();
        assertTrue(queue.isEnabled());
        assertEquals("queue:", queue.getConsumerGroupPrefix());
        assertEquals(Duration.ofSeconds(60), queue.getClaimIdleTime());
        assertEquals(10, queue.getBatchSize());
        assertEquals(Duration.ofSeconds(5), queue.getBlockTime());
        assertEquals(3, queue.getMaxRetryCount());
    }

    @Test
    void setEnabledShouldWork() {
        CacheProperties properties = new CacheProperties();
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());
    }

    @Test
    void setKeyPrefixShouldWork() {
        CacheProperties properties = new CacheProperties();
        properties.setKeyPrefix("custom:");
        assertEquals("custom:", properties.getKeyPrefix());
    }

    @Test
    void redisSettersShouldWork() {
        CacheProperties.Redis redis = new CacheProperties.Redis();
        redis.setKeyPrefix("rk:");
        redis.setDefaultTtl(Duration.ofHours(2));
        redis.setTtlSeparator("@");
        redis.setCacheNullValues(true);

        assertEquals("rk:", redis.getKeyPrefix());
        assertEquals(Duration.ofHours(2), redis.getDefaultTtl());
        assertEquals("@", redis.getTtlSeparator());
        assertTrue(redis.isCacheNullValues());
    }

    @Test
    void lockSettersShouldWork() {
        CacheProperties.Lock lock = new CacheProperties.Lock();
        lock.setEnabled(false);
        lock.setKeyPrefix("lk:");
        lock.setDefaultWaitTime(5);
        lock.setDefaultLeaseTime(60);
        lock.setDefaultTimeUnit(TimeUnit.MINUTES);
        lock.setDefaultMessage("custom msg");

        assertFalse(lock.isEnabled());
        assertEquals("lk:", lock.getKeyPrefix());
        assertEquals(5, lock.getDefaultWaitTime());
        assertEquals(60, lock.getDefaultLeaseTime());
        assertEquals(TimeUnit.MINUTES, lock.getDefaultTimeUnit());
        assertEquals("custom msg", lock.getDefaultMessage());
    }

    @Test
    void initSettersShouldWork() {
        CacheProperties.Init init = new CacheProperties.Init();
        init.setEnabled(false);
        init.setAutoRun(false);
        init.setParallel(true);
        init.setDefaultFailOnError(true);

        assertFalse(init.isEnabled());
        assertFalse(init.isAutoRun());
        assertTrue(init.isParallel());
        assertTrue(init.isDefaultFailOnError());
    }

    @Test
    void queueSettersShouldWork() {
        CacheProperties.Queue queue = new CacheProperties.Queue();
        queue.setEnabled(false);
        queue.setConsumerGroupPrefix("cg:");
        queue.setClaimIdleTime(Duration.ofMinutes(5));
        queue.setBatchSize(50);
        queue.setBlockTime(Duration.ofSeconds(10));
        queue.setMaxRetryCount(5);

        assertFalse(queue.isEnabled());
        assertEquals("cg:", queue.getConsumerGroupPrefix());
        assertEquals(Duration.ofMinutes(5), queue.getClaimIdleTime());
        assertEquals(50, queue.getBatchSize());
        assertEquals(Duration.ofSeconds(10), queue.getBlockTime());
        assertEquals(5, queue.getMaxRetryCount());
    }

    @Test
    void nestedObjectsShouldNotBeNull() {
        CacheProperties properties = new CacheProperties();
        assertNotNull(properties.getRedis());
        assertNotNull(properties.getLock());
        assertNotNull(properties.getInit());
        assertNotNull(properties.getQueue());
    }

    @Test
    void setRedisShouldWork() {
        CacheProperties properties = new CacheProperties();
        CacheProperties.Redis customRedis = new CacheProperties.Redis();
        customRedis.setKeyPrefix("custom:");
        properties.setRedis(customRedis);
        assertEquals("custom:", properties.getRedis().getKeyPrefix());
    }

    @Test
    void setLockShouldWork() {
        CacheProperties properties = new CacheProperties();
        CacheProperties.Lock customLock = new CacheProperties.Lock();
        customLock.setKeyPrefix("custom:");
        properties.setLock(customLock);
        assertEquals("custom:", properties.getLock().getKeyPrefix());
    }

    @Test
    void setInitShouldWork() {
        CacheProperties properties = new CacheProperties();
        CacheProperties.Init customInit = new CacheProperties.Init();
        customInit.setParallel(true);
        properties.setInit(customInit);
        assertTrue(properties.getInit().isParallel());
    }

    @Test
    void setQueueShouldWork() {
        CacheProperties properties = new CacheProperties();
        CacheProperties.Queue customQueue = new CacheProperties.Queue();
        customQueue.setBatchSize(100);
        properties.setQueue(customQueue);
        assertEquals(100, properties.getQueue().getBatchSize());
    }
}
