package com.lee.code.gen.util;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.entity.DataSourceConfig;
import com.lee.code.gen.core.entity.TableColumn;
import com.lee.code.gen.core.entity.TableInfo;
import com.lee.code.gen.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.*;

@Slf4j
public class DbUtil {

    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String REMARKS = "REMARKS";
    public static final String TABLE_TYPE = "TABLE_TYPE";
    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String DATA_TYPE = "DATA_TYPE";
    public static final String COLUMN_SIZE = "COLUMN_SIZE";
    public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    public static final String COLUMN_DEF = "COLUMN_DEF";
    public static final String NULLABLE = "NULLABLE";
    public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";

    private final DataSourceConfig dataSourceConfig;

    public DbUtil(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public List<TableInfo> getTables(String tableNamePattern, String[] types) {
        return getTables(dataSourceConfig.getCatalog(), dataSourceConfig.getSchema(), tableNamePattern, types);
    }

    public TableInfo getTable(String tableName) {
        return getTable(dataSourceConfig.getCatalog(), dataSourceConfig.getSchema(), tableName);
    }

    public List<TableInfo> getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) {
        List<TableInfo> tables = new ArrayList<>();
        try(Connection connection =  dataSourceConfig.getConnection();
            ResultSet resultSet = connection.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, types)) {
            TableInfo table;
            while (resultSet.next()) {
                table = new TableInfo();
                table.setName(resultSet.getString(TABLE_NAME));
                table.setComment(formatComment(resultSet.getString(REMARKS)));
                table.setType(resultSet.getString(TABLE_TYPE));
                tables.add(table);
            }
        } catch (SQLException ex) {
            log.error("获取数据库表信息失败", ex);
            throw new ServerException(ex);
        }
        return Collections.unmodifiableList(tables);
    }

    public TableInfo getTable(String catalog, String schema, String tableName) {
        TableInfo table = new TableInfo();
        try (Connection connection =  dataSourceConfig.getConnection();
             ResultSet resultSet = connection.getMetaData().getTables(catalog, schema, tableName, new String[] { "TABLE", "VIEW" })) {
            table.setName(tableName);
            while (resultSet.next()) {
                table.setComment(formatComment(resultSet.getString(REMARKS)));
                table.setType(resultSet.getString(TABLE_TYPE));
            }
        } catch (SQLException ex) {
            log.error("获取数据库表信息" + tableName + "失败", ex);
            throw new ServerException(ex);
        }
        return table;
    }

    public List<TableColumn> getColumns(String tableName, boolean queryPrimaryKey) {
        return getColumns(dataSourceConfig.getCatalog(), dataSourceConfig.getSchema(), tableName, queryPrimaryKey);
    }

    public List<TableColumn> getColumns(String catalog, String schema, String tableName, boolean queryPrimaryKey) {
        Set<String> primaryKeys = new HashSet<>();
        if (queryPrimaryKey) {
            try (Connection connection = dataSourceConfig.getConnection();
                 ResultSet resultSet = connection.getMetaData().getPrimaryKeys(catalog, schema, tableName)) {
                while (resultSet.next()) {
                    primaryKeys.add(resultSet.getString(COLUMN_NAME));
                }
            } catch (SQLException ex) {
                log.error("获取数据库表主键信息" + tableName + "失败", ex);
            }
        }
        List<TableColumn> columns = new ArrayList<>();
        try (Connection connection = dataSourceConfig.getConnection();
             ResultSet resultSet = connection.getMetaData().getColumns(catalog, schema, tableName, "%")) {
            TableColumn column;
            while (resultSet.next()) {
                column = new TableColumn();
                String columnName = resultSet.getString(COLUMN_NAME);
                column.setColumnName(columnName);
                column.setPrimaryKey(primaryKeys.contains(columnName));
                column.setTypeName(resultSet.getString(TYPE_NAME));
                column.setJdbcType(JdbcType.forCode(resultSet.getInt(DATA_TYPE)));
                column.setLength(resultSet.getInt(COLUMN_SIZE));
                column.setScale(resultSet.getInt(DECIMAL_DIGITS));
                column.setComment(formatComment(resultSet.getString(REMARKS)));
                column.setDefaultValue(resultSet.getString(COLUMN_DEF));
                column.setNullable(resultSet.getInt(NULLABLE) == DatabaseMetaData.columnNullable);
                column.setAutoIncrement("YES".equals(resultSet.getString(IS_AUTOINCREMENT)));
                columns.add(column);
            }
        } catch (SQLException ex) {
            log.error("获取数据库表字段信息" + tableName + "失败", ex);
        }
        return Collections.unmodifiableList(columns);
    }

    private String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? Constants.EMPTY_STRING : comment.replace("\r\n", "\t");
    }
}
