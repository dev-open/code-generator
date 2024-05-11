package com.lee.code.gen.aop;

import com.lee.code.gen.annoation.IPRateLimiter;
import com.lee.code.gen.exception.RequestNotPermitted;
import com.lee.code.gen.util.LocalCacheUtil;
import com.lee.code.gen.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Aspect
@Slf4j
public class IPRateLimiterAspect {

    @Pointcut("@annotation(com.lee.code.gen.annoation.IPRateLimiter)")
    public void pointCut() {}

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        IPRateLimiter rateLimiter = AnnotationUtils.getAnnotation(methodSignature.getMethod(), IPRateLimiter.class);

        // 1. 获取客户端IP
        String ip = WebUtil.getRemoteIP();
        var request = WebUtil.getRequest();
        if (ip != null && rateLimiter != null && request.isPresent()) {
            int rate = rateLimiter.rate();
            int interval = rateLimiter.interval();
            TimeUnit unit = rateLimiter.unit();

            // 2. 获取 key
            String key = String.join("_", ip, request.get().getMethod(), request.get().getRequestURI());
            synchronized (this) {
                AtomicInteger count =  LocalCacheUtil.get(key);
                // 2.1 当前客户端第一次请求
                if (count == null) {
                    // 2.2 设置缓存
                    LocalCacheUtil.put(key, new AtomicInteger(1), unit.toSeconds(interval));
                } else {
                    // 2.2 超出频率限制
                    if (count.incrementAndGet() > rate) {
                        throw new RequestNotPermitted(rate, interval, unit);
                    }
                }
            }
        }
    }
}
