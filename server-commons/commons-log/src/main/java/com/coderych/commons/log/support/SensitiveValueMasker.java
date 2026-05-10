package com.coderych.commons.log.support;

import com.coderych.commons.core.util.collection.CollectionUtils;
import com.coderych.commons.log.autoconfigure.LogProperties;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 敏感字段脱敏器，递归遍历数据结构并对匹配的字段值进行掩码处理。
 * <p>支持 Map、Collection、数组等嵌套结构，字段名匹配忽略大小写。
 * 敏感字段列表通过 {@link LogProperties#sensitiveFields} 配置。</p>
 *
 * @author YCH
 */
public class SensitiveValueMasker {

    private static final String MASK = "******";

    private final Set<String> sensitiveFields;

    public SensitiveValueMasker(LogProperties properties) {
        this.sensitiveFields = CollectionUtils.isEmpty(properties.getSensitiveFields())
                ? Set.of()
                : properties.getSensitiveFields().stream()
                  .map(this::normalize)
                  .collect(Collectors.toUnmodifiableSet());
    }

    public Object mask(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map<?, ?> map) {
            return maskMap(map);
        }
        if (value instanceof Collection<?> collection) {
            return maskCollection(collection);
        }
        if (value.getClass().isArray()) {
            return maskArray(value);
        }
        return value;
    }

    private Map<String, Object> maskMap(Map<?, ?> source) {
        Map<String, Object> masked = new LinkedHashMap<>(source.size());
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            String key = entry.getKey() == null ? "null" : entry.getKey().toString();
            Object value = isSensitiveField(key) ? MASK : mask(entry.getValue());
            masked.put(key, value);
        }
        return masked;
    }

    private List<Object> maskCollection(Collection<?> source) {
        List<Object> masked = new ArrayList<>(source.size());
        for (Object item : source) {
            masked.add(mask(item));
        }
        return masked;
    }

    private List<Object> maskArray(Object source) {
        int length = Array.getLength(source);
        List<Object> masked = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            masked.add(mask(Array.get(source, index)));
        }
        return masked;
    }

    private boolean isSensitiveField(String fieldName) {
        return sensitiveFields.contains(normalize(fieldName));
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }
}
