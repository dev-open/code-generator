package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 模板配置
 */
@Data
@Schema(description = "模板配置")
public class TemplateConfig {

    /** 项目名称 */
    @Schema(description = "项目名称")
    private String projName;

    /** 版本 */
    @Schema(description = "版本")
    private String version = "0.0.1";

    /** 作者 */
    @Schema(description = "作者")
    private String author = "unknown";

    /** 模板 */
    @Schema(description = "模板")
    private List<TemplateConfigItem> templates;

    /** 参数 */
    @Schema(description = "参数")
    private List<TemplateParameter> parameters;
}
