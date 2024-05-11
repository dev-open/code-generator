package com.lee.code.gen.lsp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * java lsp（语言服务器协议） 配置
 * <a href="https://github.com/eclipse-jdtls/eclipse.jdt.ls">Java Lsp</a>
 */
@ConfigurationProperties(prefix = "code.gen.lsp")
@Data
public class LspProperties {

    private Boolean enabled = false;

    /** the port of the socket to connect to */
    private String clientPort;

    /** the host name to connect to. If not set, defaults to localhost. */
    private String clientHost;

    /** lsp home
     * <a herf="https://download.eclipse.org/jdtls/milestones/?d">eclipse.jdt.ls</a>
     */
    private String home;

    /**
     * An absolute path to your data directory. eclipse.jdt.ls stores workspace specific information in it. This should be unique per workspace/project.
     * eclipse.jdt.ls 工作空间
     */
    private String workspace;

    /** 其他执行参数 */
    private List<String> executeOptions;
}
