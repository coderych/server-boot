package com.coderych.commons.core.util.spring;

import lombok.NoArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 注解解析工具类，支持从方法参数上解析方法级或类级注解。
 *
 * @author YCH
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AnnotationUtils {
    public static <T extends Annotation> T resolve(MethodParameter methodParameter, Class<T> annotationType) {
        T methodAnnotation = methodParameter.getMethodAnnotation(annotationType);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return methodParameter.getDeclaringClass().getAnnotation(annotationType);
    }

    public static <T extends Annotation> T resolveMerged(MethodParameter methodParameter, Class<T> annotationType) {
        MergedAnnotation<T> methodAnnotation = MergedAnnotations.from(
                Objects.requireNonNull(methodParameter.getMethod()), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
        ).get(annotationType);
        if (methodAnnotation.isPresent()) {
            return methodAnnotation.synthesize();
        }
        MergedAnnotation<T> classAnnotation = MergedAnnotations.from(
                methodParameter.getDeclaringClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
        ).get(annotationType);
        if (classAnnotation.isPresent()) {
            return classAnnotation.synthesize();
        }
        return null;
    }

    public static boolean hasAnnotation(MethodParameter methodParameter, Class<? extends Annotation> annotationType) {
        return resolve(methodParameter, annotationType) != null;
    }
}
