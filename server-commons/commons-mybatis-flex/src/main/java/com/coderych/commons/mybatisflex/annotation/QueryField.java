package com.coderych.commons.mybatisflex.annotation;

import com.coderych.commons.mybatisflex.enums.Operator;
import com.coderych.commons.mybatisflex.enums.Relation;

import java.lang.annotation.*;

/**
 * 查询字段注解，标注在实体或查询对象的字段上，
 * 由 {@link com.coderych.commons.mybatisflex.util.QueryWrapperBuilder} 解析后自动构建查询条件。
 *
 * @author YCH
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryField {

    /**
     * 查询字段名称，默认使用被标注字段名。
     */
    String[] value() default {};

    /**
     * 是否与前置条件使用 OR 连接。
     */
    boolean isOr() default false;

    /**
     * 查询操作符。
     */
    Operator operator() default Operator.EQ;

    /**
     * 与子条件的连接关系。
     */
    Relation relation() default Relation.AND;
}
