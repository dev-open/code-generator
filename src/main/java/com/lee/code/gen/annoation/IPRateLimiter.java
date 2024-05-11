package com.lee.code.gen.annoation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface IPRateLimiter {

    @AliasFor("rate")
    int value() default 10;

    /** 频率 */
    @AliasFor("value")
    int rate() default 10;

    /** 间隔 */
    int interval() default 1;

    /** 时间单位 */
    TimeUnit unit() default TimeUnit.SECONDS;
}
