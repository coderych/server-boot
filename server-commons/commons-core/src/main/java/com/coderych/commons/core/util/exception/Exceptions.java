package com.coderych.commons.core.util.exception;

import com.coderych.commons.core.exception.InternalException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 异常工具类，提供异常类型转换、根因提取和异常链判断。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Exceptions {

    public static RuntimeException unchecked(Throwable throwable) {
        if (throwable instanceof RuntimeException runtimeException) {
            return runtimeException;
        }
        if (throwable instanceof Error error) {
            throw error;
        }
        return new InternalException(throwable.getMessage(), throwable);
    }

    public static Throwable rootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    public static String rootMessage(Throwable throwable) {
        Throwable rootCause = rootCause(throwable);
        return rootCause == null ? null : rootCause.getMessage();
    }

    public static boolean causedBy(Throwable throwable, Class<? extends Throwable> type) {
        if (throwable == null || type == null) {
            return false;
        }
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
