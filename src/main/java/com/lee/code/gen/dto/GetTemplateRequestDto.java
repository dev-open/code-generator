package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取模板内容请求")
public class GetTemplateRequestDto {

    /** 工程 ID */
    @Schema(description = "工程 ID")
    private Integer projId;

    /** 模板路径 */
    @Schema(description = "模板路径")
    private String templatePath;

    /** 分支、标签或提交的名称 */
    @Schema(description = "分支、标签或提交的名称")
    private String ref = "main";
}
