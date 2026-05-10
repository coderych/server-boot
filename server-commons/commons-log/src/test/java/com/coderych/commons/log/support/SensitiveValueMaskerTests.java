package com.coderych.commons.log.support;

import com.coderych.commons.log.autoconfigure.LogProperties;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveValueMaskerTests {

    private SensitiveValueMasker createMasker(String... fields) {
        LogProperties properties = new LogProperties();
        properties.setSensitiveFields(List.of(fields));
        return new SensitiveValueMasker(properties);
    }

    @Test
    void shouldReturnNullForNullInput() {
        SensitiveValueMasker masker = createMasker("password");
        assertNull(masker.mask(null));
    }

    @Test
    void shouldReturnPlainObjectAsIs() {
        SensitiveValueMasker masker = createMasker("password");
        assertEquals("hello", masker.mask("hello"));
        assertEquals(42, masker.mask(42));
        assertEquals(true, masker.mask(true));
    }

    @Test
    void shouldMaskSensitiveFieldInMap() {
        SensitiveValueMasker masker = createMasker("password", "token");

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("username", "admin");
        input.put("password", "secret123");
        input.put("token", "abc-xyz");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertEquals("admin", result.get("username"));
        assertEquals("******", result.get("password"));
        assertEquals("******", result.get("token"));
    }

    @Test
    void shouldMaskCaseInsensitive() {
        SensitiveValueMasker masker = createMasker("password");

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("Password", "secret");
        input.put("PASSWORD", "secret");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertEquals("******", result.get("Password"));
        assertEquals("******", result.get("PASSWORD"));
    }

    @Test
    void shouldRecursivelyMaskNestedMap() {
        SensitiveValueMasker masker = createMasker("password");

        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("password", "inner-secret");
        nested.put("name", "inner-name");

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("data", nested);
        input.put("password", "outer-secret");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertEquals("******", result.get("password"));

        @SuppressWarnings("unchecked")
        Map<String, Object> resultNested = (Map<String, Object>) result.get("data");
        assertEquals("******", resultNested.get("password"));
        assertEquals("inner-name", resultNested.get("name"));
    }

    @Test
    void shouldRecursivelyMaskCollectionItems() {
        SensitiveValueMasker masker = createMasker("password");

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("password", "secret");
        item.put("name", "test");

        List<Object> input = List.of(item, "plain");

        List<?> result = (List<?>) masker.mask(input);

        @SuppressWarnings("unchecked")
        Map<String, Object> resultItem = (Map<String, Object>) result.get(0);
        assertEquals("******", resultItem.get("password"));
        assertEquals("test", resultItem.get("name"));
        assertEquals("plain", result.get(1));
    }

    @Test
    void shouldRecursivelyMaskArrayItems() {
        SensitiveValueMasker masker = createMasker("password");

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("password", "secret");
        item.put("name", "test");

        Object[] input = {item, "plain"};

        List<?> result = (List<?>) masker.mask(input);

        @SuppressWarnings("unchecked")
        Map<String, Object> resultItem = (Map<String, Object>) result.get(0);
        assertEquals("******", resultItem.get("password"));
        assertEquals("test", resultItem.get("name"));
        assertEquals("plain", result.get(1));
    }

    @Test
    void shouldNotMaskWhenNoSensitiveFieldsConfigured() {
        SensitiveValueMasker masker = createMasker();

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("password", "secret");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertEquals("secret", result.get("password"));
    }

    @Test
    void shouldHandleMapWithNullKey() {
        SensitiveValueMasker masker = createMasker("null");

        Map<String, Object> input = new LinkedHashMap<>();
        input.put(null, "value");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertEquals("******", result.get("null"));
    }

    @Test
    void shouldReturnEmptyMapForEmptyInput() {
        SensitiveValueMasker masker = createMasker("password");

        Map<String, Object> input = new LinkedHashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) masker.mask(input);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForEmptyCollection() {
        SensitiveValueMasker masker = createMasker("password");

        List<?> result = (List<?>) masker.mask(Collections.emptyList());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListForEmptyArray() {
        SensitiveValueMasker masker = createMasker("password");

        List<?> result = (List<?>) masker.mask(new Object[0]);

        assertTrue(result.isEmpty());
    }
}
