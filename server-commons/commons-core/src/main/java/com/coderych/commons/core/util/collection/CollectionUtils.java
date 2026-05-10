package com.coderych.commons.core.util.collection;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;

/**
 * 集合工具类，扩展 Hutool {@link CollUtil}，增加去重、转换等方法。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils extends CollUtil {

    public static <T> List<T> nullToEmpty(List<T> values) {
        return values == null ? Collections.emptyList() : values;
    }

    public static <T> Set<T> nullToEmpty(Set<T> values) {
        return values == null ? Collections.emptySet() : values;
    }

    public static <T> T getLast(Collection<T> values) {
        if (isEmpty(values)) {
            return null;
        }
        if (values instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<T> typedList = (List<T>) list;
            return typedList.getLast();
        }
        T last = null;
        for (T value : values) {
            last = value;
        }
        return last;
    }

    public static <T, K> List<T> distinctBy(Collection<T> values, Function<? super T, ? extends K> keyMapper) {
        if (isEmpty(values)) {
            return Collections.emptyList();
        }
        Assert.notNull(keyMapper, "keyMapper must not be null");
        Map<K, T> unique = new LinkedHashMap<>();
        for (T value : values) {
            unique.putIfAbsent(keyMapper.apply(value), value);
        }
        return new ArrayList<>(unique.values());
    }

    public static <T, K> Map<K, T> toMap(Collection<T> values, Function<? super T, ? extends K> keyMapper) {
        return toMap(values, keyMapper, Function.identity());
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> values,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends V> valueMapper) {
        if (isEmpty(values)) {
            return Collections.emptyMap();
        }
        Assert.notNull(keyMapper, "keyMapper must not be null");
        Assert.notNull(valueMapper, "valueMapper must not be null");
        Map<K, V> result = new LinkedHashMap<>(values.size());
        for (T value : values) {
            result.put(keyMapper.apply(value), valueMapper.apply(value));
        }
        return result;
    }

    public static <T> Set<T> linkedHashSetOf(Collection<T> values) {
        return values == null ? new LinkedHashSet<>() : new LinkedHashSet<>(values);
    }
}
