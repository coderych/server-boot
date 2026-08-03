package com.coderych.commons.job.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 任务调度模块配置属性。
 * <p>
 * 通过 {@code commons.job.*} 前缀配置，涵盖 XXL-Job、Quartz 调度器及异步线程池参数。
 *
 * @author YCH
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "commons.job")
public class JobProperties {

    /**
     * 是否启用任务调度模块。
     */
    private boolean enabled = true;

    /**
     * XXL-Job 配置。
     */
    private Xxl xxl = new Xxl();

    /**
     * Quartz 配置。
     */
    private Quartz quartz = new Quartz();

    /**
     * 异步线程池配置。
     */
    private Async async = new Async();

    @Getter
    @Setter
    public static class Xxl {
        /**
         * 是否启用 XXL-Job。
         */
        private boolean enabled = false;
        /**
         * XXL-Job 管理端地址。
         */
        private String adminAddresses = "http://localhost:8080/xxl-job-admin";
        /**
         * XXL-Job 访问令牌。
         */
        private String accessToken = "default_token";
        /**
         * 执行器名称。
         */
        private String appname = "xxl-job-executor";
        /**
         * 执行器注册地址。
         */
        private String address;
        /**
         * 执行器 IP 地址。
         */
        private String ip;
        /**
         * 执行器端口。
         */
        private int port = 9999;
        /**
         * 执行日志路径。
         */
        private String logPath;
        /**
         * 日志保留天数。
         */
        private int logRetentionDays = 30;
        /**
         * XXL-Job 通信超时时间。
         */
        private int timeout = 3;
    }

    @Getter
    @Setter
    public static class Quartz {
        /**
         * 是否启用 Quartz。
         */
        private boolean enabled = true;
        /**
         * Quartz 调度器名称。
         */
        private String schedulerName = "commons-job-scheduler";
        /**
         * 任务重试配置。
         */
        private Retry retry = new Retry();
    }

    @Getter
    @Setter
    public static class Retry {
        /**
         * 最大尝试次数。
         */
        private int maxAttempts = 3;
        /**
         * 初始重试延迟，单位为毫秒。
         */
        private long delayMs = 1000;
        /**
         * 重试延迟退避乘数。
         */
        private double backoffMultiplier = 2.0;
    }

    @Getter
    @Setter
    public static class Async {
        /**
         * 核心线程数。
         */
        private int corePoolSize = 8;
        /**
         * 最大线程数。
         */
        private int maxPoolSize = 16;
        /**
         * 队列容量。
         */
        private int queueCapacity = 256;
        /**
         * 线程名称前缀。
         */
        private String threadNamePrefix = "commons-job-";
        /**
         * 空闲线程存活时间。
         */
        private Duration keepAlive = Duration.ofSeconds(60);
    }
}
