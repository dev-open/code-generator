package com.lee.code.gen.dto.common;

import com.lee.code.gen.common.ValidationError;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "参数校验响应")
public class ValidationErrorResponseDto {

    /** 参数检验错误消息 */
    @Schema(description = "参数检验错误消息")
    private List<ValidationError> errors;
}
