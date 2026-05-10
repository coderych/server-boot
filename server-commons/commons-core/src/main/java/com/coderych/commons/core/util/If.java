package com.coderych.commons.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 条件判断工具类，提供统一的空值/真值判断和条件执行能力。
 * <p>支持 CharSequence、Collection、Map、Boolean、Number 等多种类型的真值判断。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class If {

    public static boolean hasValue(CharSequence value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isEmpty(CharSequence value) {
        return !hasValue(value);
    }

    public static boolean hasValue(Collection<?> value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isEmpty(Collection<?> value) {
        return !hasValue(value);
    }

    public static boolean hasValue(Map<?, ?> value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> value) {
        return !hasValue(value);
    }

    public static boolean hasValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        }
        return true;
    }

    public static boolean isEmpty(Object value) {
        return !hasValue(value);
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static boolean isFalse(Boolean value) {
        return value != null && !value;
    }

    public static boolean isNotTrue(Boolean value) {
        return value == null || !value;
    }

    public static boolean isNotFalse(Boolean value) {
        return value == null || value;
    }

    public static boolean isTrue(Number value) {
        return value != null && value.longValue() == 1;
    }

    public static boolean isFalse(Number value) {
        return value != null && value.longValue() == 0;
    }

    public static boolean isNotTrue(Number value) {
        return value == null || value.longValue() != 1;
    }

    public static boolean isNotFalse(Number value) {
        return value == null || value.longValue() != 0;
    }

    public static boolean isTrue(CharSequence value) {
        if (value == null) {
            return false;
        }
        String s = value.toString().trim().toLowerCase();
        return "true".equals(s) || "1".equals(s) || "yes".equals(s);
    }

    public static boolean isFalse(CharSequence value) {
        if (value == null) {
            return false;
        }
        String s = value.toString().trim().toLowerCase();
        return "false".equals(s) || "0".equals(s) || "no".equals(s);
    }

    /**
     * 判断值是否不为 true。
     * 注意：当值既不是 true 也不是 false 时（如 "other"），此方法返回 true。
     * 如果需要判断值是否明确为 false，请使用 {@link #isFalse(CharSequence)}。
     */
    public static boolean isNotTrue(CharSequence value) {
        return !isTrue(value);
    }

    /**
     * 判断值是否不为 false。
     * 注意：当值既不是 true 也不是 false 时（如 "other"），此方法返回 true。
     * 如果需要判断值是否明确为 true，请使用 {@link #isTrue(CharSequence)}。
     */
    public static boolean isNotFalse(CharSequence value) {
        return !isFalse(value);
    }

    public static boolean isTrue(Object value) {
        if (value instanceof Boolean b) return isTrue(b);
        if (value instanceof Number n) return isTrue(n);
        if (value instanceof CharSequence cs) return isTrue(cs);
        return false;
    }

    public static boolean isFalse(Object value) {
        if (value instanceof Boolean b) return isFalse(b);
        if (value instanceof Number n) return isFalse(n);
        if (value instanceof CharSequence cs) return isFalse(cs);
        return false;
    }

    public static boolean isNotTrue(Object value) {
        return !isTrue(value);
    }

    public static boolean isNotFalse(Object value) {
        return !isFalse(value);
    }

    public static boolean isEqual(BigDecimal a, BigDecimal b) {
        if (Objects.equals(a, b)) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.compareTo(b) == 0;
    }

    public static boolean notEquals(BigDecimal a, BigDecimal b) {
        return !isEqual(a, b);
    }

    public static boolean isEqual(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof BigDecimal bdA && b instanceof BigDecimal bdB) {
            return isEqual(bdA, bdB);
        }
        return a.equals(b);
    }

    public static boolean notEquals(Object a, Object b) {
        return !isEqual(a, b);
    }

    public static void then(boolean condition, Runnable action) {
        if (condition && action != null) {
            action.run();
        }
    }

    public static void thenElse(boolean condition, Runnable thenAction, Runnable elseAction) {
        if (condition) {
            if (thenAction != null) {
                thenAction.run();
            }
            return;
        }
        if (elseAction != null) {
            elseAction.run();
        }
    }

    public static <T> void notNull(T value, Consumer<T> consumer) {
        if (value != null && consumer != null) {
            consumer.accept(value);
        }
    }

    public static <T> T get(boolean condition, Supplier<T> supplier, T defaultValue) {
        if (condition && supplier != null) {
            return supplier.get();
        }
        return defaultValue;
    }
}
