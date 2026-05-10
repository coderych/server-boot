package com.coderych.commons.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解，标注在方法上自动加锁/解锁。
 * <p>通过 SpEL 解析 key，支持自定义等待时间、租约时间和超时提示。</p>
 *
 * @author YCH
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁名称，作为锁 key 的前缀
     */
    String name();

    /**
     * 锁 key 的业务标识，支持 SpEL 表达式
     */
    String key() default "";

    /**
     * 获取锁的最大等待时间，-1 使用全局默认值
     */
    long waitTime() default -1L;

    /**
     * 锁的持有时间，-1 使用全局默认值
     */
    long leaseTime() default -1L;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时的提示信息
     */
    String message() default "";
}
