package com.lee.code.gen.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class GitlabFeignInterceptor implements RequestInterceptor {

    private static final String HEADER_PAT = "PRIVATE-TOKEN";

    @Value("${code.gen.gitlab.pat}")
    private String pat;

    @Override
    public void apply(RequestTemplate template) {
        template.header(HEADER_PAT, pat);
    }
}
