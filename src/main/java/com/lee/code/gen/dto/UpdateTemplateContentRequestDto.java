package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新模板内容请求")
public class UpdateTemplateContentRequestDto {

    /** 工程 ID */
    @Schema(description = "工程 ID ")
    @NotNull
    private Integer projId;

    /** 模板内容 */
    @Schema(description = "模板内容")
    @NotEmpty
    private String content;

    /** 模板路径 */
    @Schema(description = "模板路径")
    @NotBlank
    private String path;
}
