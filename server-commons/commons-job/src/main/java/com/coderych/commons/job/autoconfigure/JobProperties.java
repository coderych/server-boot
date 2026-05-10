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

    private boolean enabled = true;

    private Xxl xxl = new Xxl();

    private Quartz quartz = new Quartz();

    private Async async = new Async();

    @Getter
    @Setter
    public static class Xxl {
        private boolean enabled = false;
        private String adminAddresses = "http://localhost:8080/xxl-job-admin";
        private String accessToken = "default_token";
        private String appname = "xxl-job-executor";
        private String address;
        private String ip;
        private int port = 9999;
        private String logPath;
        private int logRetentionDays = 30;
        private int timeout = 3;
    }

    @Getter
    @Setter
    public static class Quartz {
        private boolean enabled = true;
        private String schedulerName = "commons-job-scheduler";
        private Retry retry = new Retry();
    }

    @Getter
    @Setter
    public static class Retry {
        private int maxAttempts = 3;
        private long delayMs = 1000;
        private double backoffMultiplier = 2.0;
    }

    @Getter
    @Setter
    public static class Async {
        private int corePoolSize = 8;
        private int maxPoolSize = 16;
        private int queueCapacity = 256;
        private String threadNamePrefix = "commons-job-";
        private Duration keepAlive = Duration.ofSeconds(60);
    }
}
