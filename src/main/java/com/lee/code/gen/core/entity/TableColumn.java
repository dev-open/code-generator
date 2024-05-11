package com.lee.code.gen.core.entity;

import lombok.Data;
import org.apache.ibatis.type.JdbcType;

@Data
public class TableColumn {

    /** 表名称 */
    private String tableName;

    /** 字段名称 */
    private String columnName;

    /** 字段长度 */
    private int length;

    /** 是否非空 */
    private boolean nullable;

    /** 注释 */
    private String comment;

    /** 字段默认值 */
    private String defaultValue;

    /** 字段精度 */
    private int scale;

    /** 是否主键 */
    private boolean primaryKey;

    /** 类型名 */
    private String typeName;

    /** JDBC类型 */
    private JdbcType jdbcType;

    /** 是否自增 */
    private boolean autoIncrement;
}
