package com.coderych.commons.cache.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * Redis 序列化器工厂，统一管理 Key（String）和 Value（JSON）序列化器实例。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisSerializerFactory {

    private final RedisSerializer<String> STRING_SERIALIZER = StringRedisSerializer.UTF_8;

    private RedisSerializer<Object> valueSerializer;

    public RedisSerializerFactory(JsonMapper jsonMapper) {
        this.valueSerializer = new GenericJacksonJsonRedisSerializer(jsonMapper == null ? JsonMapper.builder().build() : jsonMapper);
    }

    public RedisSerializer<String> stringSerializer() {
        return STRING_SERIALIZER;
    }

    public RedisSerializer<Object> valueSerializer() {
        return valueSerializer;
    }
}
