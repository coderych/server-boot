package com.coderych.commons.log.support;

import com.coderych.commons.log.autoconfigure.LogProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParameterSerializerTests {

    private ParameterSerializer createSerializer(int maxLength) {
        LogProperties properties = new LogProperties();
        properties.setMaxLength(maxLength);
        SensitiveValueMasker masker = new SensitiveValueMasker(properties);
        return new ParameterSerializer(properties, masker);
    }

    @Test
    void shouldReturnEmptyArrayForNullArguments() {
        ParameterSerializer serializer = createSerializer(2000);
        String result = serializer.serializeArguments(null);
        assertEquals("[]", result);
    }

    @Test
    void shouldSerializeNormalArguments() {
        ParameterSerializer serializer = createSerializer(2000);
        String result = serializer.serializeArguments(new Object[]{"hello", 42});
        assertNotNull(result);
        assertTrue(result.contains("hello"));
    }

    @Test
    void shouldSkipHttpServletRequest() {
        ParameterSerializer serializer = createSerializer(2000);
        HttpServletRequest request = new MockHttpServletRequest();
        String result = serializer.serializeArguments(new Object[]{request, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
        assertFalse(result.contains("MockHttpServletRequest"));
    }

    @Test
    void shouldSkipHttpServletResponse() {
        ParameterSerializer serializer = createSerializer(2000);
        HttpServletResponse response = new MockHttpServletResponse();
        String result = serializer.serializeArguments(new Object[]{response, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipMultipartFile() {
        ParameterSerializer serializer = createSerializer(2000);
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        String result = serializer.serializeArguments(new Object[]{file, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipMultipartFileArray() {
        ParameterSerializer serializer = createSerializer(2000);
        MultipartFile[] files = {
                new MockMultipartFile("f1", "a.txt", "text/plain", "a".getBytes()),
                new MockMultipartFile("f2", "b.txt", "text/plain", "b".getBytes())
        };
        String result = serializer.serializeArguments(new Object[]{files, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipCollectionOfMultipartFiles() {
        ParameterSerializer serializer = createSerializer(2000);
        List<MultipartFile> files = List.of(
                new MockMultipartFile("f1", "a.txt", "text/plain", "a".getBytes()),
                new MockMultipartFile("f2", "b.txt", "text/plain", "b".getBytes())
        );
        String result = serializer.serializeArguments(new Object[]{files, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipInputStream() {
        ParameterSerializer serializer = createSerializer(2000);
        ByteArrayInputStream inputStream = new ByteArrayInputStream("data".getBytes());
        String result = serializer.serializeArguments(new Object[]{inputStream, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipOutputStream() {
        ParameterSerializer serializer = createSerializer(2000);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String result = serializer.serializeArguments(new Object[]{outputStream, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipByteArray() {
        ParameterSerializer serializer = createSerializer(2000);
        byte[] bytes = "data".getBytes();
        String result = serializer.serializeArguments(new Object[]{bytes, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldSkipBindingResult() {
        ParameterSerializer serializer = createSerializer(2000);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        String result = serializer.serializeArguments(new Object[]{bindingResult, "visible"});
        assertNotNull(result);
        assertTrue(result.contains("visible"));
    }

    @Test
    void shouldNotSkipNull() {
        ParameterSerializer serializer = createSerializer(2000);
        String result = serializer.serializeArguments(new Object[]{null});
        assertNotNull(result);
    }

    @Test
    void shouldTruncateLongOutput() {
        ParameterSerializer serializer = createSerializer(10);
        String result = serializer.serializeArguments(new Object[]{"a-very-long-string-value"});
        assertNotNull(result);
        assertTrue(result.endsWith("..."));
        assertTrue(result.length() <= 13);
    }

    @Test
    void shouldNotTruncateWhenMaxLengthIsZero() {
        LogProperties properties = new LogProperties();
        properties.setMaxLength(0);
        SensitiveValueMasker masker = new SensitiveValueMasker(properties);
        ParameterSerializer serializer = new ParameterSerializer(properties, masker);

        String result = serializer.serializeArguments(new Object[]{"hello"});
        assertNotNull(result);
        assertFalse(result.endsWith("..."));
    }

    @Test
    void shouldReturnNullForSkippableResult() {
        ParameterSerializer serializer = createSerializer(2000);
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        assertNull(serializer.serializeResult(file));
    }

    @Test
    void shouldSerializeNormalResult() {
        ParameterSerializer serializer = createSerializer(2000);
        String result = serializer.serializeResult("hello");
        assertNotNull(result);
        assertTrue(result.contains("hello"));
    }

    @Test
    void shouldSerializeNullResult() {
        ParameterSerializer serializer = createSerializer(2000);
        String result = serializer.serializeResult(null);
        assertNotNull(result);
        assertTrue(result.contains("null"));
    }
}
