package com.coderych.commons.cache.autoconfigure;

import com.coderych.commons.cache.aspect.LockAspect;
import com.coderych.commons.cache.init.CacheInitializerRunner;
import com.coderych.commons.cache.support.*;
import com.coderych.commons.cache.util.LockManager;
import com.coderych.commons.cache.util.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * 缓存模块自动配置类。
 * <p>注册 RedisTemplate、CacheManager、分布式锁切面、缓存初始化器和消息队列模板等 Bean。</p>
 *
 * @author YCH
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(CacheProperties.class)
@ConditionalOnProperty(prefix = "commons.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CacheAutoConfiguration {
    @Bean
    public RedisSerializerFactory redisSerializerFactory(ObjectProvider<JsonMapper> jsonMapperProvider) {
        log.info(">>>>>>>>> Bean: redisSerializerFactory —— 注册 Redis 序列化器工厂");
        return new RedisSerializerFactory(jsonMapperProvider.getIfAvailable());
    }

    @Bean(name = "cacheRedisTemplate")
    @ConditionalOnMissingBean(name = "cacheRedisTemplate")
    public RedisTemplate<String, Object> cacheRedisTemplate(RedisConnectionFactory connectionFactory, RedisSerializerFactory redisSerializerFactory) {
        log.info(">>>>>>>>> Bean: cacheRedisTemplate —— 注册缓存 RedisTemplate");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        RedisSerializer<String> keySerializer = redisSerializerFactory.stringSerializer();
        RedisSerializer<Object> valueSerializer = redisSerializerFactory.valueSerializer();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties properties, RedisSerializerFactory redisSerializerFactory) {
        log.info(">>>>>>>>> Bean: redisCacheConfiguration —— 注册 Redis 缓存配置");
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializerFactory.stringSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializerFactory.valueSerializer()))
                .entryTtl(properties.getRedis().getDefaultTtl());
        if (!properties.getRedis().isCacheNullValues()) {
            configuration = configuration.disableCachingNullValues();
        }
        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, RedisCacheConfiguration cacheConfiguration) {
        log.info(">>>>>>>>> Bean: cacheManager —— 注册缓存管理器");
        return new TtlRedisCacheManager(connectionFactory, cacheConfiguration);
    }

    @Bean
    public SmartInitializingSingleton cacheModuleInitializer(CacheProperties properties,
                                                             ObjectProvider<RedissonClient> redissonClientProvider,
                                                             ObjectProvider<RedisTemplate<String, Object>> redisTemplateProvider) {
        return () -> {
            log.info(">>>>>>>>> Bean: cacheModuleInitializer —— 初始化缓存模块");
            CacheKeyBuilder.init(properties);
            TtlCacheNameResolver.init(properties);
            redissonClientProvider.ifAvailable(redissonClient -> LockManager.init(properties, redissonClient));
            redisTemplateProvider.ifAvailable(RedisCache::init);
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "commons.cache.lock", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public LockAspect lockAspect() {
        log.info(">>>>>>>>> Bean: lockAspect —— 注册分布式锁切面");
        return new LockAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "commons.cache.init", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public CacheInitializerRunner cacheInitializerRunner(CacheProperties properties) {
        log.info(">>>>>>>>> Bean: cacheInitializerRunner —— 注册缓存初始化 Runner");
        return new CacheInitializerRunner(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "commons.cache.queue", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public RedisQueueTemplate redisQueueTemplate(CacheProperties properties, RedisTemplate<String, Object> cacheRedisTemplate) {
        log.info(">>>>>>>>> Bean: redisQueueTemplate —— 注册 Redis 消息队列模板");
        return new RedisQueueTemplate(properties, cacheRedisTemplate);
    }
}
