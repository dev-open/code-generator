package com.lee.code.gen.common;

import com.lee.code.gen.util.MessageUtil;
import com.lee.code.gen.util.SpringContextUtil;
import lombok.Getter;

@Getter
public enum CodeEnum {

    // 操作成功
    RCD0(0),
    // 系统内部错误
    RCD10000(10000),
    // 操作失败
    RCD20000(20000),
    // 远程调用400
    RCD20001(20001),
    // 参数检验失败
    RCD20002(20002),
    // 频率限制
    RCD20003(20003),
    // Json校验失败
    RCD20004(20004),
    // 404
    RCD40000(40000);

    // 响应码
    private final Integer code;

    // 消息
    private final String message;

    CodeEnum(Integer code) {
        this.code = code;
        MessageUtil messageUtil = SpringContextUtil.getBean(MessageUtil.class);
        this.message = messageUtil.getMsg(String.valueOf(code));
    }
}
