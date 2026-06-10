package com.coderych.commons.cache.autoconfigure;

import com.coderych.commons.cache.aspect.LockAspect;
import com.coderych.commons.cache.init.CacheInitializerRunner;
import com.coderych.commons.cache.support.RedisQueueTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CacheAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class))
            .withBean("redisConnectionFactory", RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class))
            .withBean("cacheRedisTemplate", RedisTemplate.class, () -> mock(RedisTemplate.class));

    @Test
    void shouldRegisterCachePropertiesBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CacheProperties.class);
        });
    }

    @Test
    void shouldRegisterCacheRedisTemplateBean() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("cacheRedisTemplate");
        });
    }

    @Test
    void shouldRegisterRedisCacheConfigurationBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RedisCacheConfiguration.class);
        });
    }

    @Test
    void shouldRegisterCacheManagerBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CacheManager.class);
        });
    }

    @Test
    void shouldRegisterLockAspectBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LockAspect.class);
        });
    }

    @Test
    void shouldRegisterCacheInitializerRunnerBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CacheInitializerRunner.class);
        });
    }

    @Test
    void shouldRegisterRedisQueueTemplateBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RedisQueueTemplate.class);
        });
    }

    @Test
    void shouldNotRegisterBeansWhenDisabled() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class))
                .withPropertyValues("commons.cache.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CacheProperties.class);
                    assertThat(context).doesNotHaveBean(RedisCacheConfiguration.class);
                    assertThat(context).doesNotHaveBean(CacheManager.class);
                    assertThat(context).doesNotHaveBean(LockAspect.class);
                    assertThat(context).doesNotHaveBean(CacheInitializerRunner.class);
                    assertThat(context).doesNotHaveBean(RedisQueueTemplate.class);
                });
    }

    @Test
    void shouldNotRegisterLockAspectWhenLockDisabled() {
        contextRunner
                .withPropertyValues("commons.cache.lock.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(LockAspect.class);
                });
    }

    @Test
    void shouldNotRegisterCacheInitializerRunnerWhenInitDisabled() {
        contextRunner
                .withPropertyValues("commons.cache.init.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CacheInitializerRunner.class);
                });
    }

    @Test
    void shouldNotRegisterRedisQueueTemplateWhenQueueDisabled() {
        contextRunner
                .withPropertyValues("commons.cache.queue.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(RedisQueueTemplate.class);
                });
    }

    @Test
    void shouldBindCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "commons.cache.key-prefix=myprefix:",
                        "commons.cache.redis.key-prefix=rc:",
                        "commons.cache.redis.default-ttl=PT1H",
                        "commons.cache.lock.key-prefix=lk:"
                )
                .run(context -> {
                    CacheProperties props = context.getBean(CacheProperties.class);
                    assertThat(props.getKeyPrefix()).isEqualTo("myprefix:");
                    assertThat(props.getRedis().getKeyPrefix()).isEqualTo("rc:");
                    assertThat(props.getRedis().getDefaultTtl()).isEqualTo(java.time.Duration.ofHours(1));
                    assertThat(props.getLock().getKeyPrefix()).isEqualTo("lk:");
                });
    }
}
