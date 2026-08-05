package com.coderych.commons.core.util;

import cn.hutool.core.bean.BeanUtil;
import io.github.linpeilie.Converter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 对象转换工具类，基于 MapStruct Plus，转换失败时自动降级为 JSON 转换。
 *
 * @author YCH
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BEAN {

    @Getter
    private static volatile Converter converter = new Converter();

    public static synchronized void init(Converter converter) {
        if (converter != null) {
            BEAN.converter = converter;
        }
    }

    public static synchronized void reset() {
        converter = new Converter();
    }

    public static <S, T> T convert(S source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        if (targetType.isInstance(source)) {
            return targetType.cast(source);
        }
        try {
            return converter.convert(source, targetType);
        } catch (RuntimeException exception) {
            log.warn("MapStruct conversion failed, falling back to JSON conversion for type: {}", targetType.getName(), exception);
            return JSON.convert(source, targetType);
        }
    }

    public static <S, T> T convert(S source, Class<T> targetType, Consumer<T> consumer) {
        T target = convert(source, targetType);
        if (target != null && consumer != null) {
            consumer.accept(target);
        }
        return target;
    }

    public static <S, T> T copy(S source, T target) {
        return converter.convert(source, target);
    }

    public static <S, T> T copy(S source, T target, Consumer<T> consumer) {
        return converter.convert(source, target, consumer);
    }

    public static <S, T> List<T> convertList(List<S> source, Class<T> targetType) {
        if (source == null) {
            return List.of();
        }
        try {
            return converter.convert(source, targetType);
        } catch (RuntimeException exception) {
            log.warn("MapStruct list conversion failed, falling back to element-by-element conversion for type: {}", targetType.getName(), exception);
            return source.stream().map(item -> convert(item, targetType)).toList();
        }
    }

    public static <S, T> List<T> convertList(List<S> source, Class<T> targetType, Consumer<T> consumer) {
        List<T> targets = convertList(source, targetType);
        if (consumer != null) {
            targets.forEach(consumer);
        }
        return targets;
    }

    public static <T> T convert(Map<String, Object> source, Class<T> targetType) {
        try {
            return converter.convert(source, targetType);
        } catch (RuntimeException exception) {
            log.warn("MapStruct conversion from Map failed, falling back to JSON conversion for type: {}", targetType.getName(), exception);
            return JSON.convert(source, targetType);
        }
    }

    public static Map<String, Object> toMap(Object source) {
        return BeanUtil.beanToMap(source);
    }

    public static Map<String, Object> toMap(Object source, String... ignoreProperties) {
        Map<String, Object> map = BeanUtil.beanToMap(source);
        if (ignoreProperties != null) {
            for (String ignoreProperty : ignoreProperties) {
                map.remove(ignoreProperty);
            }
        }
        return map;
    }
}
