package com.coderych.commons.cache.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 缓存模块配置属性，前缀 {@code commons.cache}。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.cache")
public class CacheProperties {

    private boolean enabled = true;

    private String keyPrefix = "app:";

    private Redis redis = new Redis();

    private Lock lock = new Lock();

    private Init init = new Init();

    private Queue queue = new Queue();

    /**
     * Redis 缓存配置
     */
    @Getter
    @Setter
    public static class Redis {

        private String keyPrefix = "cache:";

        private Duration defaultTtl = Duration.ofMinutes(30);

        private String ttlSeparator = "#";

        private boolean cacheNullValues = false;
    }

    /**
     * 分布式锁配置
     */
    @Getter
    @Setter
    public static class Lock {

        private boolean enabled = true;

        private String keyPrefix = "lock:";

        private long defaultWaitTime = 3;

        private long defaultLeaseTime = 30;

        private TimeUnit defaultTimeUnit = TimeUnit.SECONDS;

        private String defaultMessage = "获取锁失败";
    }

    /**
     * 缓存初始化配置
     */
    @Getter
    @Setter
    public static class Init {

        private boolean enabled = true;

        private boolean autoRun = true;

        private boolean parallel = false;

        private boolean defaultFailOnError = false;
    }

    /**
     * Redis Stream 消息队列配置
     */
    @Getter
    @Setter
    public static class Queue {

        private boolean enabled = true;

        private String consumerGroupPrefix = "queue:";

        private Duration claimIdleTime = Duration.ofSeconds(60);

        private int batchSize = 10;

        private Duration blockTime = Duration.ofSeconds(5);

        private int maxRetryCount = 3;
    }
}
