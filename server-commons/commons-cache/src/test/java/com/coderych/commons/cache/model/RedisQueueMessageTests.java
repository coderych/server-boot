package com.coderych.commons.cache.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RedisQueueMessageTests {

    @Test
    void noArgConstructorShouldGenerateId() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>();
        assertNotNull(message.getId());
        assertDoesNotThrow(() -> UUID.fromString(message.getId()));
    }

    @Test
    void noArgConstructorShouldGenerateTimestamp() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>();
        assertNotNull(message.getTimestamp());
    }

    @Test
    void noArgConstructorShouldHaveNullPayload() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>();
        assertNull(message.getPayload());
    }

    @Test
    void noArgConstructorShouldHaveZeroRetryCount() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>();
        assertEquals(0, message.getRetryCount());
    }

    @Test
    void payloadConstructorShouldSetPayload() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>("hello");
        assertEquals("hello", message.getPayload());
    }

    @Test
    void payloadConstructorShouldGenerateId() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>("hello");
        assertNotNull(message.getId());
        assertDoesNotThrow(() -> UUID.fromString(message.getId()));
    }

    @Test
    void payloadConstructorShouldGenerateTimestamp() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>("hello");
        assertNotNull(message.getTimestamp());
    }

    @Test
    void payloadConstructorShouldHaveZeroRetryCount() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>("hello");
        assertEquals(0, message.getRetryCount());
    }

    @Test
    void twoMessagesShouldHaveDifferentIds() {
        RedisQueueMessage<String> m1 = new RedisQueueMessage<>("a");
        RedisQueueMessage<String> m2 = new RedisQueueMessage<>("b");
        assertNotEquals(m1.getId(), m2.getId());
    }

    @Test
    void settersShouldWork() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>();
        message.setId("custom-id");
        message.setPayload("value");
        message.setRetryCount(5);
        Instant now = Instant.now();
        message.setTimestamp(now);

        assertEquals("custom-id", message.getId());
        assertEquals("value", message.getPayload());
        assertEquals(5, message.getRetryCount());
        assertEquals(now, message.getTimestamp());
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        RedisQueueMessage<String> m1 = new RedisQueueMessage<>();
        m1.setId("same-id");
        m1.setPayload("same");
        m1.setTimestamp(Instant.EPOCH);
        m1.setRetryCount(1);

        RedisQueueMessage<String> m2 = new RedisQueueMessage<>();
        m2.setId("same-id");
        m2.setPayload("same");
        m2.setTimestamp(Instant.EPOCH);
        m2.setRetryCount(1);

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void toStringShouldContainFields() {
        RedisQueueMessage<String> message = new RedisQueueMessage<>("test");
        String str = message.toString();
        assertTrue(str.contains(message.getId()));
        assertTrue(str.contains("test"));
    }
}
