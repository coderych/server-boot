package com.coderych.commons.core.util.json;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BigNumberSerializerTests {

    private static JsonMapper createMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Number.class, BigNumberSerializer.INSTANCE);
        return JsonMapper.builder().addModule(module).build();
    }

    @Test
    void shouldSerializeNormalLongAsNumber() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", 42L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("42"));
        assertFalse(json.contains("\"42\""));
    }

    @Test
    void shouldSerializeLargeLongAsString() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", 9007199254740992L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("\"9007199254740992\""));
    }

    @Test
    void shouldSerializeSmallNegativeLongAsString() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", -9007199254740992L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("\"-9007199254740992\""));
    }

    @Test
    void shouldSerializeSafeIntegerBoundaryAsNumber() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", 9007199254740991L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("9007199254740991"));
    }

    @Test
    void shouldSerializeNegativeSafeIntegerBoundaryAsNumber() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", -9007199254740991L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("-9007199254740991"));
    }

    @Test
    void shouldSerializeZeroAsNumber() throws Exception {
        JsonMapper mapper = createMapper();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("value", 0L);
        String json = mapper.writeValueAsString(map);
        assertTrue(json.contains("0"));
    }

    @Test
    void instanceShouldNotBeNull() {
        assertNotNull(BigNumberSerializer.INSTANCE);
    }
}
