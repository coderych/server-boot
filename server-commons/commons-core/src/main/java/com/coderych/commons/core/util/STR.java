package com.coderych.commons.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 字符串工具类，扩展 Hutool {@link StrUtil}，增加空值转换和拼接方法。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class STR extends StrUtil {

    public static String emptyToNull(String value) {
        return isEmpty(value) ? null : value;
    }

    public static String nullToEmpty(String value) {
        return value == null ? EMPTY : value;
    }

    public static String blankToNull(String value) {
        return isBlank(value) ? null : value;
    }

    public static String removeAllBlank(CharSequence value) {
        if (value == null) {
            return null;
        }
        return value.toString().replaceAll("\\s+", EMPTY);
    }

    public static String joinIfNotBlank(CharSequence delimiter, CharSequence... values) {
        return Arrays.stream(values)
                .filter(StrUtil::isNotBlank)
                .map(CharSequence::toString)
                .collect(Collectors.joining(delimiter == null ? EMPTY : delimiter.toString()));
    }
}
