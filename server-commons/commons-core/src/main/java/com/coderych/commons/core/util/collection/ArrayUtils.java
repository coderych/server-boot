package com.coderych.commons.core.util.collection;

import cn.hutool.core.util.ArrayUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 数组工具类，扩展 Hutool {@link ArrayUtil}。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArrayUtils extends ArrayUtil {

    public static <T> T[] nullToEmpty(T[] values, Class<T> componentType) {
        return values == null ? newArray(componentType, 0) : values;
    }

    public static <T> T first(T[] values) {
        return isEmpty(values) ? null : values[0];
    }

    public static <T> T last(T[] values) {
        return isEmpty(values) ? null : values[values.length - 1];
    }
}
