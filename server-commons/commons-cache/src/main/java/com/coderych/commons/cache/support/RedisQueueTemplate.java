package com.coderych.commons.cache.support;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.cache.model.RedisQueueMessage;
import com.coderych.commons.core.util.STR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 基于 Redis Stream 的消息队列模板。
 * <p>支持消息发布（offer）、消费（poll/consume）、确认（ack），
 * 以及超限重试后自动转入死信队列。</p>
 *
 * @author YCH
 */
@Slf4j
public class RedisQueueTemplate {

    /**
     * 消息载荷字段名。
     */
    private static final String PAYLOAD_FIELD = "payload";

    private static final String RETRY_COUNT_FIELD = "retryCount";

    /**
     * 时间戳字段名。
     */
    private static final String TIMESTAMP_FIELD = "timestamp";

    /**
     * 死信队列名称后缀。
     */
    private static final String DEAD_LETTER_SUFFIX = ":dead-letter";

    /**
     * 缓存模块配置。
     */
    private final CacheProperties properties;

    /**
     * Redis 操作模板。
     */
    private final RedisTemplate<String, Object> redisTemplate;

    private final Set<String> createdGroups = ConcurrentHashMap.newKeySet();

    public RedisQueueTemplate(CacheProperties properties,
                              RedisTemplate<String, Object> redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    public String offer(String queueName, Object value) {
        RedisQueueMessage<Object> message = new RedisQueueMessage<>(value);
        RecordId recordId = streamOperations().add(ObjectRecord.create(streamKey(queueName), message));
        return recordId == null ? null : recordId.getValue();
    }

    public <T> T poll(String queueName,
                      String consumerGroup,
                      String consumerName,
                      Class<T> targetType,
                      Duration timeout) {
        ObjectRecord<String, RedisQueueMessage> record = read(queueName, consumerGroup, consumerName, timeout);
        if (record == null) {
            return null;
        }
        return targetType.cast(record.getValue().getPayload());
    }

    public <T> void consume(String queueName,
                            String consumerGroup,
                            String consumerName,
                            Class<T> targetType,
                            Duration timeout,
                            Consumer<T> consumer) {
        ObjectRecord<String, RedisQueueMessage> record = read(queueName, consumerGroup, consumerName, timeout);
        if (record == null) {
            return;
        }
        try {
            consumer.accept(targetType.cast(record.getValue().getPayload()));
            ack(queueName, consumerGroup, record.getId().getValue());
        } catch (RuntimeException exception) {
            requeue(queueName, consumerGroup, record);
            throw exception;
        }
    }

    public void ack(String queueName, String consumerGroup, String messageId) {
        streamOperations().acknowledge(streamKey(queueName), groupName(consumerGroup), RecordId.of(messageId));
    }

    private ObjectRecord<String, RedisQueueMessage> read(String queueName,
                                                         String consumerGroup,
                                                         String consumerName,
                                                         Duration timeout) {
        ensureGroup(queueName, consumerGroup);
        List<ObjectRecord<String, RedisQueueMessage>> records = streamOperations().read(RedisQueueMessage.class,
                org.springframework.data.redis.connection.stream.Consumer.from(groupName(consumerGroup), consumerName),
                StreamReadOptions.empty().count(1).block(timeout == null ? properties.getQueue().getBlockTime() : timeout),
                StreamOffset.create(streamKey(queueName), ReadOffset.lastConsumed()));
        return records == null || records.isEmpty() ? null : records.getFirst();
    }

    private void requeue(String queueName, String consumerGroup, ObjectRecord<String, RedisQueueMessage> record) {
        int retryCount = record.getValue().getRetryCount();
        if (retryCount >= properties.getQueue().getMaxRetryCount()) {
            moveToDeadLetter(queueName, consumerGroup, record);
            return;
        }
        RedisQueueMessage<?> message = record.getValue();
        message.setRetryCount(retryCount + 1);
        streamOperations().add(ObjectRecord.create(streamKey(queueName), message));
        ack(queueName, consumerGroup, record.getId().getValue());
    }

    private void moveToDeadLetter(String queueName, String consumerGroup, ObjectRecord<String, RedisQueueMessage> record) {
        String deadLetterKey = streamKey(queueName) + DEAD_LETTER_SUFFIX;
        RedisQueueMessage<?> message = record.getValue();
        message.setRetryCount(message.getRetryCount() + 1);
        streamOperations().add(ObjectRecord.create(deadLetterKey, message));
        ack(queueName, consumerGroup, record.getId().getValue());
        log.warn("Message exceeded max retry count, moved to dead letter queue: {}", deadLetterKey);
    }

    private void ensureGroup(String queueName, String consumerGroup) {
        String groupKey = queueName + ":" + consumerGroup;
        if (createdGroups.contains(groupKey)) {
            return;
        }
        String streamKey = streamKey(queueName);
        String group = groupName(consumerGroup);
        try {
            streamOperations().createGroup(streamKey, ReadOffset.latest(), group);
            createdGroups.add(groupKey);
        } catch (Exception exception) {
            String message = exception.getMessage();
            if (message != null && message.contains("BUSYGROUP")) {
                createdGroups.add(groupKey);
            } else {
                throw exception;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private StreamOperations<String, Object, Object> streamOperations() {
        return (StreamOperations<String, Object, Object>) (StreamOperations<?, ?, ?>) redisTemplate.opsForStream();
    }

    private String streamKey(String queueName) {
        return CacheKeyBuilder.buildQueueKey(queueName);
    }

    private String groupName(String consumerGroup) {
        String prefix = properties.getQueue().getConsumerGroupPrefix();
        if (STR.isBlank(consumerGroup)) {
            return prefix;
        }
        return CacheKeyBuilder.build(prefix, consumerGroup);
    }
}
