package com.lee.code.gen.config;

import com.lee.code.gen.core.filter.RestFilter;
import com.lee.code.gen.filter.ForwardFilter;
import com.lee.code.gen.filter.TraceFilter;
import com.lee.code.gen.interceptor.RestResponseInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.controller.path-prefix}")
    private String pathPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        RestResponseInterceptor restResponseInterceptor = new RestResponseInterceptor();

        registry.addInterceptor(restResponseInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui/**");
    }

    @Bean
    public FilterRegistrationBean<RestFilter> traceIdFilter() {
        var filter = new FilterRegistrationBean<RestFilter>();
        filter.setFilter(new TraceFilter().excludePathPatterns("/swagger-ui/**", "/assets/**", "/monacoeditorwork/**", "/favicon.ico", "/index.html", "/"));
        filter.addUrlPatterns("/*");
        filter.setName("traceIdFilter");
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filter;
    }

    @Bean
    public FilterRegistrationBean<RestFilter> indexRouterFilter() {
        var filter = new FilterRegistrationBean<RestFilter>();
        filter.setFilter(new ForwardFilter("/index.html"));
        filter.addUrlPatterns("/ui/*", "/404");
        filter.setName("indexRouterFilter");
        return filter;
    }

    @Bean
    public FilterRegistrationBean<RestFilter> loginRouterFilter() {
        var filter = new FilterRegistrationBean<RestFilter>();
        filter.setFilter(new ForwardFilter("/login.html"));
        filter.addUrlPatterns("/login");
        filter.setName("loginRouterFilter");
        return filter;
    }

    /**
     * 为接口配置统一前缀
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(pathPrefix, c -> c.isAnnotationPresent(RestController.class) || c.isAnnotationPresent(Controller.class));
    }
}
