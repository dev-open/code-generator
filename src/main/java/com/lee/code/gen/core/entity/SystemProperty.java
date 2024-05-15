package com.lee.code.gen.core.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "系统属性")
public class SystemProperty {

    @Schema(description = "属性名")
    @NotBlank
    private String name;

    @Schema(description = "属性值")
    private Object value;
}
