package com.coderych.commons.core.util.validation;

import com.coderych.commons.core.util.spring.SpringUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 参数校验工具类，基于 Jakarta Validation，支持手动触发校验。
 * <p>优先从 Spring 容器获取 Validator，未初始化时使用默认实现。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {

    private static volatile Validator fallbackValidator;

    public static <T> void validate(T value, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator().validate(value, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static <T> void validateProperty(T value, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator().validateProperty(value, propertyName, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static <T> boolean isValid(T value, Class<?>... groups) {
        return validator().validate(value, groups).isEmpty();
    }

    private static Validator validator() {
        try {
            return SpringUtils.getBean(Validator.class);
        } catch (RuntimeException exception) {
            return fallbackValidator();
        }
    }

    private static Validator fallbackValidator() {
        if (fallbackValidator == null) {
            synchronized (ValidationUtils.class) {
                if (fallbackValidator == null) {
                    fallbackValidator = Validation.buildDefaultValidatorFactory().getValidator();
                }
            }
        }
        return fallbackValidator;
    }
}
