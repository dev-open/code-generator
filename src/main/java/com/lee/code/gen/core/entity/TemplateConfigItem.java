package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板配置子项
 */
@Data
@Schema(description = "模板配置子项")
public class TemplateConfigItem {

    /** 模板名称 */
    @Schema(description = "模板名称")
    private String templateName;

    /** 模板路径 */
    @Schema(description = "模板路径")
    private String templatePath;

    /** 生成代码的目标路径 */
    @Schema(description = "生成代码的目标路径")
    private String targetPath;

    /** 是否启用 */
    @Schema(description = "是否启用")
    private Boolean enable;
}
