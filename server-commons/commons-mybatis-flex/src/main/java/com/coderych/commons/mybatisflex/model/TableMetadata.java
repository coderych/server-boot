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

    private String tableName;

    private String remarks;

    private List<ColumnMetadata> columns;
}
