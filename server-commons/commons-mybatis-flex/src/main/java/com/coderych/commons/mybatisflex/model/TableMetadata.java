package com.coderych.commons.mybatisflex.model;

import lombok.Data;

import java.util.List;

/**
 * 数据库表元数据，包含表名、备注及其列信息列表。
 *
 * @author YCH
 */
@Data
public class TableMetadata {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 表备注
     */
    private String remarks;

    /**
     * 列信息列表
     */
    private List<ColumnMetadata> columns;
}
