package com.lee.code.gen.dto;

import com.lee.code.gen.core.entity.FieldInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Schema(description = "获取表实体请求响应")
public class GetTableEntityResponseDto {

    /** 实体名（首字母大写） */
    @Schema(description = "实体名")
    private String entityName;

    /** 表名 */
    @Schema(description = "表名")
    private String tableName;

    /** 注释 */
    @Schema(description = "注释")
    private String comment;

    /** 列 */
    @Schema(description = "字段信息")
    private List<FieldInfo> fields;

    /** 包名集合 */
    @Schema(description = "包名集合")
    private Set<String> importList;
}
