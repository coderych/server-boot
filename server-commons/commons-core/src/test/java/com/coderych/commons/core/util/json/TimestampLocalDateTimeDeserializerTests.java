package com.coderych.commons.core.util.json;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimestampLocalDateTimeDeserializerTests {

    private static JsonMapper createMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);
        return JsonMapper.builder().addModule(module).build();
    }

    @Test
    void shouldDeserializeTimestampToLocalDateTime() throws Exception {
        JsonMapper mapper = createMapper();
        LocalDateTime expected = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        long millis = expected.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String json = String.valueOf(millis);
        LocalDateTime result = mapper.readValue(json, LocalDateTime.class);
        assertEquals(expected, result);
    }

    @Test
    void shouldDeserializeEpochTimestamp() throws Exception {
        JsonMapper mapper = createMapper();
        LocalDateTime result = mapper.readValue("0", LocalDateTime.class);
        LocalDateTime expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        assertEquals(expected, result);
    }

    @Test
    void shouldBeConsistentWithSerializer() throws Exception {
        JsonMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
                        .addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE))
                .build();
        LocalDateTime original = LocalDateTime.of(2025, 6, 15, 12, 30, 0);
        String json = mapper.writeValueAsString(original);
        LocalDateTime restored = mapper.readValue(json, LocalDateTime.class);
        assertEquals(original, restored);
    }

    @Test
    void instanceShouldNotBeNull() {
        assertNotNull(TimestampLocalDateTimeDeserializer.INSTANCE);
    }
}
