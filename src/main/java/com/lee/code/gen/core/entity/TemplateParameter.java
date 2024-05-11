package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模板参数")
public class TemplateParameter {

    /** 参数名 */
    @Schema(description = "参数名")
    private String name;

    /** 参数值 */
    @Schema(description = "参数值")
    private Object value;

    /** 默认值 */
    @Schema(description = "默认值")
    private Object defaultValue;

    /** 是否启用 */
    @Schema(description = "是否启用")
    private Boolean enable;

    /** 描述 */
    @Schema(description = "描述")
    private String desc;

    /** 参数值的 Java 类型 */
    @Schema(description = "参数值的 Java 类型")
    private String valueClass;

    /** 默认值的 Java 类型 */
    @Schema(description = "默认值的 Java 类型")
    private String defaultValueClass;
}
