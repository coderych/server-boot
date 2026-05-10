package com.coderych.commons.cache.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Redis Stream 消息队列的消息载体，包含消息体、重试次数和时间戳。
 *
 * @param <T> 消息体类型
 * @author YCH
 */
@Data
@NoArgsConstructor
public class RedisQueueMessage<T> {

    private String id = UUID.randomUUID().toString();

    private T payload;

    private Instant timestamp = Instant.now();

    private int retryCount;

    public RedisQueueMessage(T payload) {
        this.payload = payload;
    }

}
