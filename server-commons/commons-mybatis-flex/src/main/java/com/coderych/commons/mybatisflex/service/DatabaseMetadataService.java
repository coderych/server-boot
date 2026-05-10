package com.coderych.commons.mybatisflex.service;

import com.coderych.commons.mybatisflex.model.ColumnMetadata;
import com.coderych.commons.mybatisflex.model.TableMetadata;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据库元数据服务，通过 JDBC {@link DatabaseMetaData} 读取表结构和列信息。
 *
 * @author YCH
 */
@RequiredArgsConstructor
public class DatabaseMetadataService {

    private final DataSource dataSource;

    public List<TableMetadata> getTableMetadata(String catalog, String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = null;

            List<TableMetadata> tables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                while (rs.next()) {
                    TableMetadata table = new TableMetadata();
                    table.setTableName(rs.getString("TABLE_NAME"));
                    table.setRemarks(rs.getString("REMARKS"));
                    table.setColumns(getColumnMetadata(metaData, catalog, schema, table.getTableName()));
                    tables.add(table);
                }
            }
            return tables;
        }
    }


    private List<ColumnMetadata> getColumnMetadata(DatabaseMetaData metaData, String catalog, String schema, String tableName) throws SQLException {

        Set<String> primaryKeys = new HashSet<>();
        try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        }

        List<ColumnMetadata> columns = new ArrayList<>();
        try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
            while (rs.next()) {
                ColumnMetadata column = new ColumnMetadata();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setTypeName(rs.getString("TYPE_NAME"));
                column.setColumnSize(rs.getInt("COLUMN_SIZE"));
                column.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                column.setNullable(rs.getInt("NULLABLE"));
                column.setRemarks(rs.getString("REMARKS"));
                column.setDefaultValue(rs.getString("COLUMN_DEF"));
                column.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
                column.setPrimaryKey(primaryKeys.contains(column.getColumnName()));
                columns.add(column);
            }
        }
        return columns;
    }


    public List<String> getTableNames(String catalog) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(catalog, null, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }
            return tableNames;
        }
    }
}
