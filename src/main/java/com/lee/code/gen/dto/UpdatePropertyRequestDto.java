package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "更新 property 请求")
@Data
public class UpdatePropertyRequestDto {

    @Schema(description = "属性")
    @NotBlank
    private String property;

    @Schema(description = "属性值")
    private Object value;
}
