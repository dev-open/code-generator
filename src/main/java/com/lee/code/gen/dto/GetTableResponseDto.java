package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取数据库表响应")
public class GetTableResponseDto {

    /** 表名 */
    @Schema(description = "表名")
    private String tableName;

    /** 注释 */
    @Schema(description = "注释")
    private String comment;

    /** 类型 */
    @Schema(description = "类型")
    private String type;

    /** 获取模板参数 token */
    @Schema(description = "获取表实体信息的token")
    private String accessToken;
}
