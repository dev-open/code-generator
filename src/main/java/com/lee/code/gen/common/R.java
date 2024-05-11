package com.lee.code.gen.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lee.code.gen.util.MessageUtil;
import com.lee.code.gen.util.SpringContextUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "统一响应结果")
public class R<T> {

    // 调用结果状态
    @Schema(description = "调用结果状态")
    private Boolean success;

    // 响应码
    @Schema(description = "响应码")
    private Integer code;

    // 详细信息
    @Schema(description = "详细信息")
    private String message;

    // 时间
    @Schema(description = "时间")
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT_WITH_SSS)
    private LocalDateTime time = LocalDateTime.now();

    // 响应数据
    @Schema(description = "响应数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setCode(CodeEnum.RCD0.getCode());
        r.setMessage(CodeEnum.RCD0.getMessage());
        r.setSuccess(true);
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(CodeEnum.RCD0.getCode());
        r.setMessage(CodeEnum.RCD0.getMessage());
        r.setSuccess(true);
        r.setData(data);
        return r;
    }

    public static <T> R<T> optFail() {
        R<T> r = new R<>();
        r.setCode(CodeEnum.RCD20000.getCode());
        r.setMessage(CodeEnum.RCD20000.getMessage());
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> optFail(Integer code) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(getMsg(code));
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> optFail(T data) {
        R<T> r = new R<>();
        r.setCode(CodeEnum.RCD20000.getCode());
        r.setMessage(CodeEnum.RCD20000.getMessage());
        r.setSuccess(false);
        r.setData(data);
        return r;
    }

    public static <T> R<T> optFail(Integer code, Object... params) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(getMsg(code, params));
        r.setSuccess(false);
        return r;
    }

    public static <T> R<T> optFail(CodeEnum code, T data) {
        R<T> r = new R<>();
        r.setCode(code.getCode());
        r.setMessage(code.getMessage());
        r.setSuccess(false);
        r.setData(data);
        return r;
    }

    public static <T> R<T> internalFail() {
        R<T> r = new R<>();
        r.setCode(CodeEnum.RCD10000.getCode());
        r.setMessage(CodeEnum.RCD10000.getMessage());
        r.setSuccess(false);
        return r;
    }

    private static String getMsg(Integer code, Object... args) {
        MessageUtil messageUtil = SpringContextUtil.getBean(MessageUtil.class);
        return messageUtil.getMsg(String.valueOf(code), args);
    }
}
