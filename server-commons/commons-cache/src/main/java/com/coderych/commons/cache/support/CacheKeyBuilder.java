package com.coderych.commons.cache.support;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.core.util.STR;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Redis Key 构建器，统一管理 key 前缀拼接规则。
 * <p>格式：{@code {全局前缀}{子前缀}{业务key}}，如 {@code app:cache:user:1001}。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheKeyBuilder {

    @Getter
    private static volatile CacheProperties cacheProperties;

    public static void init(CacheProperties cacheProperties) {
        if (cacheProperties == null) {
            throw new IllegalArgumentException("CacheProperties must not be null");
        }
        CacheKeyBuilder.cacheProperties = cacheProperties;
    }

    public static String buildRedisKey(String key) {
        return build(cacheProperties.getRedis().getKeyPrefix(), key);
    }

    public static String buildLockKey(String name, String businessKey) {
        String suffix = STR.isBlank(businessKey) ? name : name + ":" + businessKey;
        return build(cacheProperties.getLock().getKeyPrefix(), suffix);
    }

    public static String buildQueueKey(String queueName) {
        return build(cacheProperties.getQueue().getConsumerGroupPrefix(), queueName);
    }

    public static String build(String subPrefix, String key) {
        StringBuilder builder = new StringBuilder();
        append(builder, cacheProperties.getKeyPrefix());
        append(builder, subPrefix);
        builder.append(key);
        return builder.toString();
    }

    private static void append(StringBuilder builder, String value) {
        if (STR.isNotBlank(value)) {
            builder.append(value);
        }
    }
}
