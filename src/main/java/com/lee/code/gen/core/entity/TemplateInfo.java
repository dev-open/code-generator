package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板信息
 */
@Schema(description = "模板信息")
@Data
public class TemplateInfo {

    /** 模板内容 */
    @Schema(description = "模板内容")
    private String templateContent;

    /** 生成代码的目标路径 */
    @Schema(description = "生成代码的目标路径")
    private String targetPath;
}
