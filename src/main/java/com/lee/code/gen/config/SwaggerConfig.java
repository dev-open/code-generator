package com.lee.code.gen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "springdoc.swagger-ui.enable", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {

    private License license() {
        return new License()
                .name("MIT")
                .url("https://opensource.org/licenses/MIT");
    }

    private Info info() {
        return new Info()
                .title("Code Generator API")
                .description("代码生成器 API 文档")
                .version("v1.0.0")
                .license(license());
    }

    @Bean
    public OpenAPI codeGenOpenAPI() {
        return new OpenAPI()
                .info(info());
    }
}
