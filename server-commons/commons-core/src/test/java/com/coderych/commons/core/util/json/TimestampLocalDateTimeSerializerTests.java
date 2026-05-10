package com.coderych.commons.core.util.json;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class TimestampLocalDateTimeSerializerTests {

    private static JsonMapper createMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE);
        return JsonMapper.builder().addModule(module).build();
    }

    @Test
    void shouldSerializeLocalDateTimeToTimestamp() throws Exception {
        JsonMapper mapper = createMapper();
        LocalDateTime ldt = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        long expected = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String json = mapper.writeValueAsString(ldt);
        assertEquals(String.valueOf(expected), json);
    }

    @Test
    void shouldSerializeEpochAsTimestamp() throws Exception {
        JsonMapper mapper = createMapper();
        LocalDateTime epoch = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        long expected = epoch.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String json = mapper.writeValueAsString(epoch);
        assertEquals(String.valueOf(expected), json);
    }

    @Test
    void shouldSerializeAsNumberNotString() throws Exception {
        JsonMapper mapper = createMapper();
        LocalDateTime ldt = LocalDateTime.of(2025, 6, 15, 12, 30, 0);
        String json = mapper.writeValueAsString(ldt);
        assertFalse(json.startsWith("\""));
        assertTrue(Long.parseLong(json) > 0);
    }

    @Test
    void instanceShouldNotBeNull() {
        assertNotNull(TimestampLocalDateTimeSerializer.INSTANCE);
    }
}
