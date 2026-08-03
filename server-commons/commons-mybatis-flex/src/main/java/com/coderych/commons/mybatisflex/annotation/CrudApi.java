package com.coderych.commons.mybatisflex.annotation;

import com.coderych.commons.mybatisflex.enums.Api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CRUD 接口控制注解，用于标记在 {@link com.coderych.commons.mybatisflex.controller.BaseController} 子类上，
 * 通过 includes/excludes 白名单/黑名单机制控制可用的 CRUD 接口。
 * <p>includes 和 excludes 不能同时指定，否则会抛出异常。</p>
 *
 * @author YCH
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudApi {
    /**
     * 禁用的 CRUD 接口。
     */
    Api[] excludes() default {};

    /**
     * 启用的 CRUD 接口。
     */
    Api[] includes() default {};
}
