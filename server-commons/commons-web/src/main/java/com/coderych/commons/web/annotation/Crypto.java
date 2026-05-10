package com.coderych.commons.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加解密注解，标记在 Controller 类或方法上，配合 {@code GlobalRequestBodyAdvice} 和 {@code GlobalResponseBodyAdvice}
 * 实现请求体自动解密和响应体自动加密。
 *
 * @author YCH
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Crypto {

    boolean decrypt() default true;

    boolean encrypt() default true;

    String algorithm();
}
