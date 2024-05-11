package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "获取数据库表请求")
public class GetTableRequestDto {

    /** 主机 */
    @Schema(description = "主机")
    @NotBlank
    private String host;

    /** 端口 */
    @Schema(description = "端口")
    @NotBlank
    private Integer port;

    @Schema(description = "数据库名")
    @NotBlank
    private String dbName;

    /** 用户名 */
    @Schema(description = "用户名")
    @NotBlank
    private String username;

    /** 密码 */
    @Schema(description = "密码")
    @NotNull
    @NotBlank
    private String password;

    /** 模式 */
    @Schema(description = "模式")
    private String schema;

    /** 目录 */
    @Schema(description = "目录")
    private String catalog;

    /** 数据库类型 */
    private int dbType;
}
