package com.lee.code.gen.config;

import com.lee.code.gen.lsp.LspProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(LspProperties.class)
@Configuration
public class LspConfig {
}
