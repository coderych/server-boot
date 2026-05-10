package com.coderych.commons.mybatisflex.enums;

/**
 * 查询操作符枚举，定义 {@link com.coderych.commons.mybatisflex.annotation.QueryField} 支持的比较运算类型。
 *
 * @author YCH
 */
public enum Operator {
    EQ,

    NE,

    GT,

    LT,

    GE,

    LE,

    LIKE,

    NOT_LIKE,

    IN,

    NOT_IN,

    IS_NULL,

    IS_NOT_NULL,

    BETWEEN,

    NOT_BETWEEN,
}
