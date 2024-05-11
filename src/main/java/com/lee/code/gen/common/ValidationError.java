package com.lee.code.gen.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "参数检验错误消息")
public class ValidationError {

    /** 字段 */
    @Schema(description = "字段")
    private String field;

    /** 错误消息 */
    @Schema(description = "错误消息")
    private String errorMsg;
}
