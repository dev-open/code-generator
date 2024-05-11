package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实体字段信息
 */
@Data
@Schema(description = "实体字段信息")
public class FieldInfo {

    /** 实体字段名 */
    @Schema(description = "实体字段名")
    private String name;

    /** 字段名 */
    @Schema(description = "字段名")
    private String columnName;

    /** Java类型 */
    @Schema(description = "Java类型")
    private String javaType;

    /** 注释 */
    @Schema(description = "注释")
    private String comment;

    /** 是否主键 */
    @Schema(description = "是否主键")
    private Boolean isPrimaryKey;

    /** 是否自增 */
    @Schema(description = "是否自增")
    private Boolean isAutoIncrement;
}
