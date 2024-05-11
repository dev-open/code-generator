package com.lee.code.gen.exception;

import com.lee.code.gen.common.Constants;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    /** 业务消息码 */
    private final Integer msgCode;
    /** 消息参数 */
    private final transient Object[] params;

    public BizException(Throwable e, Integer msgCode, Object... params) {
        super(e.getMessage(), e);
        this.msgCode = msgCode;
        this.params = params;
    }

    public BizException(Throwable e, Integer msgCode) {
        super(e.getMessage(), e);
        this.msgCode = msgCode;
        this.params = Constants.EMPTY_OBJECT_ARRAY;
    }

    public BizException(Integer msgCode, Object... params) {
        this.msgCode = msgCode;
        this.params = params;
    }

    public BizException(Integer msgCode) {
        this.msgCode = msgCode;
        this.params = Constants.EMPTY_OBJECT_ARRAY;
    }
}
