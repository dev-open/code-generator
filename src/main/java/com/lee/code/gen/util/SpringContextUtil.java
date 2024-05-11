package com.lee.code.gen.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    @SuppressWarnings("all")
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        SpringContextUtil.context = ctx;
    }

    @SuppressWarnings("unchecked")
    public static  <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static  <T> T getBean(Class<?> clazz) {
        return (T) context.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static  <T> T getBean(String name, Class<?> clazz) {
        return (T) context.getBean(name, clazz);
    }

    public static String getProperty(String name) {
        return context.getEnvironment().getProperty(name);
    }
}
