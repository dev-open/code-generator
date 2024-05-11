package com.lee.code.gen.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设置模板参数请求")
public class SetTemplateParametersRequestDto {

    /** 模板参数 */
    @Valid
    @Schema(description = "模板参数")
    private List<TemplateParameterRequestDto> parameters;
}
