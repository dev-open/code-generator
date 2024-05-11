package com.lee.code.gen.config;

import com.lee.code.gen.lsp.JavaLspServerChannel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@ConditionalOnProperty(prefix = "code.gen.lsp", name = "enabled", havingValue = "true")
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(JavaLspServerChannel.class);
        return exporter;
    }
}
