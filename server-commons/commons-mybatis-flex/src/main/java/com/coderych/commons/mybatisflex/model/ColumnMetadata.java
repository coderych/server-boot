package com.coderych.commons.mybatisflex.model;

import lombok.Data;

/**
 * 数据库列元数据，封装列名、类型、大小、是否主键、默认值等信息。
 *
 * @author YCH
 */
@Data
public class ColumnMetadata {

    private String columnName;

    private String typeName;

    private int columnSize;

    private int decimalDigits;

    private int nullable;

    private String remarks;

    private boolean primaryKey;

    private String defaultValue;

    private boolean autoIncrement;
}
