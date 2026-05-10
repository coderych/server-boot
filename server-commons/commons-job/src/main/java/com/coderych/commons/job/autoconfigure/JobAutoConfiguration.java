package com.coderych.commons.job.autoconfigure;

import com.coderych.commons.core.util.STR;
import com.coderych.commons.job.model.JobRetryPolicy;
import com.coderych.commons.job.util.QuartzJobManager;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;

/**
 * 任务调度模块自动配置类。
 * <p>
 * 自动装配 Quartz 调度器、任务线程池、重试策略，以及可选的 XXL-Job 执行器。
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(JobProperties.class)
@ConditionalOnProperty(prefix = "commons.job", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolTaskExecutor jobTaskExecutor(JobProperties properties) {
        log.info(">>>>>>>>> Bean: jobTaskExecutor —— 注册任务线程池");
        JobProperties.Async async = properties.getAsync();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(async.getCorePoolSize());
        executor.setMaxPoolSize(async.getMaxPoolSize());
        executor.setQueueCapacity(async.getQueueCapacity());
        executor.setThreadNamePrefix(async.getThreadNamePrefix());
        executor.setKeepAliveSeconds(Math.toIntExact(async.getKeepAlive().getSeconds()));
        executor.initialize();
        return executor;
    }

    @Bean
    public SmartInitializingSingleton jobModuleInitializer(Scheduler scheduler, JobProperties properties) {
        return () -> {
            log.info(">>>>>>>>> Bean: jobModuleInitializer —— 初始化任务调度模块（Quartz 调度器、重试策略）");
            JobProperties.Retry retry = properties.getQuartz().getRetry();
            QuartzJobManager.init(scheduler, new JobRetryPolicy(retry.getMaxAttempts(), Duration.ofMillis(retry.getDelayMs()), retry.getBackoffMultiplier()));
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBeanCustomizer jobSchedulerFactoryBeanCustomizer(ThreadPoolTaskExecutor jobTaskExecutor) {
        log.info(">>>>>>>>> Bean: jobSchedulerFactoryBeanCustomizer —— 注册 Quartz 调度器自定义器");
        return schedulerFactoryBean -> schedulerFactoryBean.setTaskExecutor(jobTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "commons.job.xxl", name = "enabled", havingValue = "true")
    public XxlJobSpringExecutor xxlJobSpringExecutor(JobProperties properties) {
        log.info(">>>>>>>>> Bean: xxlJobSpringExecutor —— 注册 XXL-Job 执行器");
        JobProperties.Xxl xxl = properties.getXxl();
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(xxl.getAdminAddresses());
        executor.setAccessToken(xxl.getAccessToken());
        executor.setAppname(xxl.getAppname());
        executor.setTimeout(xxl.getTimeout());
        executor.setPort(xxl.getPort());
        executor.setLogRetentionDays(xxl.getLogRetentionDays());

        if (STR.isNotBlank(xxl.getAddress())) {
            executor.setAddress(xxl.getAddress());
        }
        if (STR.isNotBlank(xxl.getIp())) {
            executor.setIp(xxl.getIp());
        }
        if (STR.isNotBlank(xxl.getLogPath())) {
            executor.setLogPath(xxl.getLogPath());
        }

        return executor;
    }

}
