package com.lee.code.gen.exception;

import com.lee.code.gen.common.ValidationError;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.List;

/**
 * Json Schema 校验失败异常
 */
@Getter
public class JsonSchemaValidationException extends RuntimeException {

    /** 错误消息集合 */
    private final transient List<ValidationError> errors;
    /** Json Schema */
    private final String schema;

    public JsonSchemaValidationException(List<ValidationError> errors, String schema) {
        super(MessageFormat.format("Json validation failed, JsonSchema: {0}", schema));
        this.errors = errors;
        this.schema = schema;
    }
}
