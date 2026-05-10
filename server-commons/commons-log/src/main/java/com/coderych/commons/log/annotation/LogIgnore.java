package com.coderych.commons.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在类或方法上，表示该控制器的请求不需要自动记录操作日志。
 * <p>可作用于类级别（忽略整个控制器）或方法级别（忽略单个接口）。</p>
 *
 * @author YCH
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIgnore {
}
