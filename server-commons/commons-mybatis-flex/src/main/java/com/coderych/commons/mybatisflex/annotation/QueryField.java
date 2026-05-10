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

    String[] value() default {};

    boolean isOr() default false;

    Operator operator() default Operator.EQ;

    Relation relation() default Relation.AND;
}
