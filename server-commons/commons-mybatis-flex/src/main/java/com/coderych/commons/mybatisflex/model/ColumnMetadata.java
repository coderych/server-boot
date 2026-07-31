package com.coderych.commons.mybatisflex.model;

import lombok.Data;

/**
 * 数据库列元数据，封装列名、类型、大小、是否主键、默认值等信息。
 *
 * @author YCH
 */
@Data
public class ColumnMetadata {
    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据类型名称，如 VARCHAR、INT
     */
    private String typeName;

    /**
     * 列大小，即最大字符数或字节数
     */
    private int columnSize;

    /**
     * 小数位数
     */
    private int decimalDigits;

    /**
     * 是否可空，0-否，1-是
     */
    private int nullable;

    /**
     * 列备注
     */
    private String remarks;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否自增
     */
    private boolean autoIncrement;
}
