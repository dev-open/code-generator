package com.lee.code.gen.interceptor;

import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

public class RestResponseInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            final Class<?> clazz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();

            if (clazz.isAnnotationPresent(RestResponse.class)) {
                request.setAttribute(Constants.REST_RESPONSE_ATTR, clazz.getAnnotation(RestResponse.class));
            } else if (method.isAnnotationPresent(RestResponse.class)) {
                request.setAttribute(Constants.REST_RESPONSE_ATTR, method.getAnnotation(RestResponse.class));
            }
        }
        return true;
    }
}
