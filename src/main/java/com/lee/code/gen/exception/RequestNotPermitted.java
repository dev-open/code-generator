package com.lee.code.gen.exception;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * 频率限制异常
 */
@Getter
public class RequestNotPermitted extends RuntimeException {

    /** 频率 */
    private final Integer rate;
    /** 间隔 */
    private final Integer interval;
    /** 单位 */
    private final TimeUnit unit;

    public RequestNotPermitted(Integer rate, Integer interval, TimeUnit unit) {
        this.rate = rate;
        this.interval = interval;
        this.unit = unit;
    }
}
