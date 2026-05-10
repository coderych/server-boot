package com.coderych.commons.job.autoconfigure;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JobPropertiesTests {

    @Test
    void shouldBindDefaultValues() {
        JobProperties properties = new JobProperties();

        assertTrue(properties.isEnabled());
        assertNotNull(properties.getXxl());
        assertNotNull(properties.getQuartz());
        assertNotNull(properties.getAsync());
    }

    @Test
    void shouldBindDefaultXxlValues() {
        JobProperties.Xxl xxl = new JobProperties.Xxl();

        assertFalse(xxl.isEnabled());
        assertEquals("http://localhost:8080/xxl-job-admin", xxl.getAdminAddresses());
        assertEquals("default_token", xxl.getAccessToken());
        assertEquals("xxl-job-executor", xxl.getAppname());
        assertNull(xxl.getAddress());
        assertNull(xxl.getIp());
        assertEquals(9999, xxl.getPort());
        assertNull(xxl.getLogPath());
        assertEquals(30, xxl.getLogRetentionDays());
        assertEquals(3, xxl.getTimeout());
    }

    @Test
    void shouldBindDefaultQuartzValues() {
        JobProperties.Quartz quartz = new JobProperties.Quartz();

        assertTrue(quartz.isEnabled());
        assertEquals("commons-job-scheduler", quartz.getSchedulerName());
        assertNotNull(quartz.getRetry());
    }

    @Test
    void shouldBindDefaultRetryValues() {
        JobProperties.Retry retry = new JobProperties.Retry();

        assertEquals(3, retry.getMaxAttempts());
        assertEquals(1000L, retry.getDelayMs());
        assertEquals(2.0, retry.getBackoffMultiplier());
    }

    @Test
    void shouldBindDefaultAsyncValues() {
        JobProperties.Async async = new JobProperties.Async();

        assertEquals(8, async.getCorePoolSize());
        assertEquals(16, async.getMaxPoolSize());
        assertEquals(256, async.getQueueCapacity());
        assertEquals("commons-job-", async.getThreadNamePrefix());
        assertEquals(Duration.ofSeconds(60), async.getKeepAlive());
    }

    @Test
    void shouldSetAndGetXxlValues() {
        JobProperties.Xxl xxl = new JobProperties.Xxl();

        xxl.setEnabled(true);
        xxl.setAdminAddresses("http://example.com:8080");
        xxl.setAccessToken("custom_token");
        xxl.setAppname("my-app");
        xxl.setAddress("http://localhost:9999");
        xxl.setIp("192.168.1.1");
        xxl.setPort(8888);
        xxl.setLogPath("/var/log/xxl");
        xxl.setLogRetentionDays(7);
        xxl.setTimeout(10);

        assertTrue(xxl.isEnabled());
        assertEquals("http://example.com:8080", xxl.getAdminAddresses());
        assertEquals("custom_token", xxl.getAccessToken());
        assertEquals("my-app", xxl.getAppname());
        assertEquals("http://localhost:9999", xxl.getAddress());
        assertEquals("192.168.1.1", xxl.getIp());
        assertEquals(8888, xxl.getPort());
        assertEquals("/var/log/xxl", xxl.getLogPath());
        assertEquals(7, xxl.getLogRetentionDays());
        assertEquals(10, xxl.getTimeout());
    }

    @Test
    void shouldSetAndGetQuartzValues() {
        JobProperties.Quartz quartz = new JobProperties.Quartz();

        quartz.setEnabled(false);
        quartz.setSchedulerName("custom-scheduler");

        assertFalse(quartz.isEnabled());
        assertEquals("custom-scheduler", quartz.getSchedulerName());
    }

    @Test
    void shouldSetAndGetRetryValues() {
        JobProperties.Retry retry = new JobProperties.Retry();

        retry.setMaxAttempts(5);
        retry.setDelayMs(2000L);
        retry.setBackoffMultiplier(3.0);

        assertEquals(5, retry.getMaxAttempts());
        assertEquals(2000L, retry.getDelayMs());
        assertEquals(3.0, retry.getBackoffMultiplier());
    }

    @Test
    void shouldSetAndGetAsyncValues() {
        JobProperties.Async async = new JobProperties.Async();

        async.setCorePoolSize(4);
        async.setMaxPoolSize(8);
        async.setQueueCapacity(128);
        async.setThreadNamePrefix("custom-");
        async.setKeepAlive(Duration.ofSeconds(120));

        assertEquals(4, async.getCorePoolSize());
        assertEquals(8, async.getMaxPoolSize());
        assertEquals(128, async.getQueueCapacity());
        assertEquals("custom-", async.getThreadNamePrefix());
        assertEquals(Duration.ofSeconds(120), async.getKeepAlive());
    }

    @Test
    void shouldSetEnabled() {
        JobProperties properties = new JobProperties();

        properties.setEnabled(false);

        assertFalse(properties.isEnabled());
    }
}
