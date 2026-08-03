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

    /**
     * 是否启用缓存模块。
     */
    private boolean enabled = true;

    /**
     * 缓存键前缀。
     */
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

        /**
         * 普通缓存键前缀。
         */
        private String keyPrefix = "cache:";

        /**
         * 默认缓存过期时间。
         */
        private Duration defaultTtl = Duration.ofMinutes(30);

        /**
         * TTL 信息与缓存键之间的分隔符。
         */
        private String ttlSeparator = "#";

        /**
         * 是否缓存空值。
         */
        private boolean cacheNullValues = false;
    }

    /**
     * 分布式锁配置
     */
    @Getter
    @Setter
    public static class Lock {

        /**
         * 是否启用分布式锁。
         */
        private boolean enabled = true;

        /**
         * 分布式锁键前缀。
         */
        private String keyPrefix = "lock:";

        /**
         * 默认等待时间。
         */
        private long defaultWaitTime = 3;

        /**
         * 默认租约时间。
         */
        private long defaultLeaseTime = 30;

        /**
         * 默认时间单位。
         */
        private TimeUnit defaultTimeUnit = TimeUnit.SECONDS;

        /**
         * 获取锁失败时的默认提示。
         */
        private String defaultMessage = "获取锁失败";
    }

    /**
     * 缓存初始化配置
     */
    @Getter
    @Setter
    public static class Init {

        /**
         * 是否启用缓存初始化。
         */
        private boolean enabled = true;

        /**
         * 是否自动执行初始化器。
         */
        private boolean autoRun = true;

        /**
         * 是否并行执行初始化器。
         */
        private boolean parallel = false;

        /**
         * 初始化失败时是否默认终止启动。
         */
        private boolean defaultFailOnError = false;
    }

    /**
     * Redis Stream 消息队列配置
     */
    @Getter
    @Setter
    public static class Queue {

        /**
         * 是否启用 Redis 消息队列。
         */
        private boolean enabled = true;

        /**
         * 消费者组名称前缀。
         */
        private String consumerGroupPrefix = "queue:";

        /**
         * 消息认领的最小空闲时间。
         */
        private Duration claimIdleTime = Duration.ofSeconds(60);

        /**
         * 批量消费数量。
         */
        private int batchSize = 10;

        /**
         * 阻塞读取时间。
         */
        private Duration blockTime = Duration.ofSeconds(5);

        /**
         * 最大重试次数。
         */
        private int maxRetryCount = 3;
    }
}
