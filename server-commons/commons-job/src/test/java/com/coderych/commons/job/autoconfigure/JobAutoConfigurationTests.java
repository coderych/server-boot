package com.coderych.commons.job.autoconfigure;

import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JobAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JobAutoConfiguration.class))
            .withUserConfiguration(MockSchedulerConfiguration.class);

    @Test
    void shouldAutoConfigureWhenEnabledByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JobProperties.class);
            assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
            assertThat(context).hasBean("jobModuleInitializer");
            assertThat(context).hasBean("jobSchedulerFactoryBeanCustomizer");
        });
    }

    @Test
    void shouldNotAutoConfigureWhenDisabled() {
        contextRunner
                .withPropertyValues("commons.job.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(JobAutoConfiguration.class);
                });
    }

    @Test
    void shouldBindJobProperties() {
        contextRunner.run(context -> {
            JobProperties properties = context.getBean(JobProperties.class);
            assertThat(properties.isEnabled()).isTrue();
            assertThat(properties.getQuartz().getRetry().getMaxAttempts()).isEqualTo(3);
            assertThat(properties.getQuartz().getRetry().getDelayMs()).isEqualTo(1000L);
            assertThat(properties.getQuartz().getRetry().getBackoffMultiplier()).isEqualTo(2.0);
        });
    }

    @Test
    void shouldConfigureThreadPoolTaskExecutor() {
        contextRunner.run(context -> {
            ThreadPoolTaskExecutor executor = context.getBean(ThreadPoolTaskExecutor.class);
            assertThat(executor).isNotNull();
            assertThat(executor.getCorePoolSize()).isEqualTo(8);
            assertThat(executor.getMaxPoolSize()).isEqualTo(16);
            assertThat(executor.getThreadNamePrefix()).isEqualTo("commons-job-");
        });
    }

    @Test
    void shouldBindCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "commons.job.async.core-pool-size=4",
                        "commons.job.async.max-pool-size=8",
                        "commons.job.async.thread-name-prefix=custom-",
                        "commons.job.quartz.retry.max-attempts=5",
                        "commons.job.quartz.retry.delay-ms=2000"
                )
                .run(context -> {
                    ThreadPoolTaskExecutor executor = context.getBean(ThreadPoolTaskExecutor.class);
                    assertThat(executor.getCorePoolSize()).isEqualTo(4);
                    assertThat(executor.getMaxPoolSize()).isEqualTo(8);
                    assertThat(executor.getThreadNamePrefix()).isEqualTo("custom-");

                    JobProperties properties = context.getBean(JobProperties.class);
                    assertThat(properties.getQuartz().getRetry().getMaxAttempts()).isEqualTo(5);
                    assertThat(properties.getQuartz().getRetry().getDelayMs()).isEqualTo(2000L);
                });
    }

    @Test
    void shouldNotCreateXxlJobExecutorByDefault() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean("xxlJobSpringExecutor");
        });
    }

    @Test
    void shouldUseCustomJobTaskExecutorWhenProvided() {
        contextRunner
                .withUserConfiguration(CustomExecutorConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ThreadPoolTaskExecutor.class);
                    ThreadPoolTaskExecutor executor = context.getBean(ThreadPoolTaskExecutor.class);
                    assertThat(executor.getCorePoolSize()).isEqualTo(2);
                });
    }

    @Configuration
    static class MockSchedulerConfiguration {
        @Bean
        Scheduler scheduler() {
            return mock(Scheduler.class);
        }
    }

    @Configuration
    static class CustomExecutorConfiguration {
        @Bean
        ThreadPoolTaskExecutor jobTaskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(2);
            executor.setMaxPoolSize(4);
            executor.initialize();
            return executor;
        }
    }
}
