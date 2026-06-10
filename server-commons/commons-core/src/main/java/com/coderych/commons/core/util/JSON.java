package com.coderych.commons.core.util;

import com.coderych.commons.core.exception.BadRequestException;
import com.coderych.commons.core.exception.InternalException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

/**
 * JSON 工具类，基于 Jackson，支持序列化、反序列化、类型转换和 JSON 校验。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JSON {

    @Getter
    private static volatile JsonMapper jsonMapper = JsonMapper.builder().build();

    public static synchronized void init(JsonMapper jsonMapper) {
        if (jsonMapper != null) {
            JSON.jsonMapper = jsonMapper;
        }
    }

    public static synchronized void reset() {
        jsonMapper = JsonMapper.builder().build();
    }

    public static String toJson(Object value) {
        try {
            return jsonMapper.writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new InternalException("Failed to serialize object to JSON", exception);
        }
    }

    public static String toPrettyJson(Object value) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new InternalException("Failed to serialize object to pretty JSON", exception);
        }
    }

    public static <T> T parseObject(String json, Class<T> targetType) {
        try {
            return jsonMapper.readValue(json, targetType);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to parse JSON to " + targetType.getName(), exception);
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return jsonMapper.readValue(json, typeReference);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to parse JSON to target type", exception);
        }
    }

    public static <T> List<T> parseList(String json, Class<T> elementType) {
        try {
            JavaType targetType = jsonMapper.getTypeFactory().constructCollectionType(List.class, elementType);
            return jsonMapper.readValue(json, targetType);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to parse JSON array to " + elementType.getName(), exception);
        }
    }

    public static Map<String, Object> toMap(String json) {
        return parseObject(json, new TypeReference<>() {
        });
    }

    public static <T> T convert(Object value, Class<T> targetType) {
        try {
            return jsonMapper.convertValue(value, targetType);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to convert value to " + targetType.getName(), exception);
        }
    }

    public static <T> T convert(Object value, TypeReference<T> typeReference) {
        try {
            return jsonMapper.convertValue(value, typeReference);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to convert value to target type", exception);
        }
    }

    public static JsonNode readTree(String json) {
        try {
            return jsonMapper.readTree(json);
        } catch (JacksonException exception) {
            throw new BadRequestException("Failed to parse JSON tree", exception);
        }
    }

    public static boolean isValid(String json) {
        if (STR.isBlank(json)) {
            return false;
        }
        try {
            jsonMapper.readTree(json);
            return true;
        } catch (JacksonException exception) {
            return false;
        }
    }
}
