package com.lee.code.gen.util;

import com.lee.code.gen.common.DateType;
import com.lee.code.gen.common.JavaType;
import com.lee.code.gen.core.entity.TableColumn;
import lombok.Setter;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class TypeUtil {

    private TypeUtil() {}

    private static final Map<Integer, JavaType> TYPE_REPOSITORY = new HashMap<>();
    @Setter
    private static DateType dateType = DateType.UTIL;

    static {
        // byte[]
        TYPE_REPOSITORY.put(Types.BINARY, JavaType.BYTE_ARRAY);
        TYPE_REPOSITORY.put(Types.BLOB, JavaType.BYTE_ARRAY);
        TYPE_REPOSITORY.put(Types.LONGVARBINARY, JavaType.BYTE_ARRAY);
        TYPE_REPOSITORY.put(Types.VARBINARY, JavaType.BYTE_ARRAY);
        // byte
        TYPE_REPOSITORY.put(Types.TINYINT, JavaType.BYTE);
        // long
        TYPE_REPOSITORY.put(Types.BIGINT, JavaType.LONG);
        // boolean
        TYPE_REPOSITORY.put(Types.BIT, JavaType.BOOLEAN);
        TYPE_REPOSITORY.put(Types.BOOLEAN, JavaType.BOOLEAN);
        // short
        TYPE_REPOSITORY.put(Types.SMALLINT, JavaType.SHORT);
        // string
        TYPE_REPOSITORY.put(Types.CHAR, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.CLOB, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.VARCHAR, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.LONGVARCHAR, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.LONGNVARCHAR, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.NCHAR, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.NCLOB, JavaType.STRING);
        TYPE_REPOSITORY.put(Types.NVARCHAR, JavaType.STRING);
        // date
        TYPE_REPOSITORY.put(Types.DATE, JavaType.DATE);
        // timestamp
        TYPE_REPOSITORY.put(Types.TIMESTAMP, JavaType.TIMESTAMP);
        // double
        TYPE_REPOSITORY.put(Types.FLOAT, JavaType.DOUBLE);
        TYPE_REPOSITORY.put(Types.REAL, JavaType.DOUBLE);
        TYPE_REPOSITORY.put(Types.DOUBLE, JavaType.DOUBLE);
        // int
        TYPE_REPOSITORY.put(Types.INTEGER, JavaType.INTEGER);
        //bigDecimal
        TYPE_REPOSITORY.put(Types.NUMERIC, JavaType.BIG_DECIMAL);
        TYPE_REPOSITORY.put(Types.DECIMAL, JavaType.BIG_DECIMAL);
    }

    /**
     * JavaType 转换
     *
     * @param column 表列
     * @param defaultJavaType 默认 JavaType
     * @return {@link JavaType}
     */
    public static JavaType convert2JavaType(TableColumn column, JavaType defaultJavaType) {
        int typeCode =  column.getJdbcType().TYPE_CODE;
        return switch (typeCode) {
            case Types.BIT -> getBitType(column);
            case Types.DATE -> getDateType();
            case Types.TIME -> getTimeType();
            case Types.DECIMAL, Types.NUMERIC -> getNumber(column);
            case Types.TIMESTAMP -> getTimestampType();
            default -> TYPE_REPOSITORY.getOrDefault(typeCode, defaultJavaType);
        };
    }

    /**
     * JavaType 转换
     * @param column 表列
     * @return {@link JavaType}
     */
    public static JavaType convert2JavaType(TableColumn column) {
        return convert2JavaType(column, JavaType.OBJECT);
    }

    private static JavaType getBitType(TableColumn column) {
        if (column.getLength() > 1) {
            return JavaType.BYTE_ARRAY;
        }
        return JavaType.BOOLEAN;
    }

    private static JavaType getNumber(TableColumn column) {
        if (column.getScale() > 0 || column.getLength() > 18) {
            return TYPE_REPOSITORY.get(column.getJdbcType().TYPE_CODE);
        } else if (column.getLength() > 9) {
            return JavaType.LONG;
        } else if (column.getLength() > 4) {
            return JavaType.INTEGER;
        } else {
            return JavaType.SHORT;
        }
    }

    private static JavaType getDateType() {
        return switch (dateType) {
            case UTIL -> JavaType.DATE;
            case SQL -> JavaType.DATE_SQL;
            case TIME -> JavaType.LOCAL_DATE;
        };
    }

    private static JavaType getTimeType() {
        if (dateType == DateType.TIME) {
            return JavaType.LOCAL_TIME;
        }
        return JavaType.TIME;
    }

    private static JavaType getTimestampType() {
        return switch (dateType) {
            case TIME -> JavaType.LOCAL_DATE_TIME;
            case UTIL -> JavaType.DATE;
            case SQL -> JavaType.TIME;
        };
    }
}
