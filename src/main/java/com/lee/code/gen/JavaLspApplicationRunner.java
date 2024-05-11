package com.lee.code.gen;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.lsp.LspProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Order(2)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "code.gen.lsp", name = "enabled", havingValue = "true")
public class JavaLspApplicationRunner implements ApplicationRunner {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String JAVA_HOME = "JAVA_HOME";
    private final LspProperties properties;
    private final Cache<String, Object> localCache;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1.1 获取 JAVA_HOME
        String javaHome = System.getenv(JAVA_HOME);
        if (!StringUtils.hasText(javaHome)) {
            log.warn("开启 LSP 服务请配置环境变量【JAVA_HOME】");
            return;
        }
        log.debug("LSP 服务运行的JAVA_HOME: {}", javaHome);

        // 1.2 获取 Jar
        String jar = getLauncher();
        if (jar == null) {
            log.warn("LSP Home: {}中，没有发现可执行 jar 文件：org.eclipse.equinox.launcher_*.jar。", properties.getHome());
            return;
        }

        // 1.3 获取配置
        String configPath = getConfigPath();
        if (configPath == null) {
            log.warn("LSP Home: {}中，没有发现配置目录：{}。", properties.getHome(), OS.contains("windows") ? "config_win" : "config_linux");
            return;
        }

        // 2. 拼接 CMD
        List<String> commands = new ArrayList<>();
        commands.add(Path.of(javaHome,"bin", "java").toString());
        commands.add("-Declipse.application=org.eclipse.jdt.ls.core.id1");
        commands.add("-Dosgi.bundles.defaultStartLevel=4");
        commands.add("-Declipse.product=org.eclipse.jdt.ls.core.product");
        commands.add("-Dlog.level=ALL");
        commands.add("-Xmx1G");

        // 2.1 追加其他参数
        if (!CollectionUtils.isEmpty(properties.getExecuteOptions())) {
            commands.addAll(properties.getExecuteOptions());
        }

        commands.add("--add-modules=ALL-SYSTEM");
        commands.add("--add-opens");
        commands.add("java.base/java.util=ALL-UNNAMED");
        commands.add("--add-opens");
        commands.add("java.base/java.lang=ALL-UNNAMED");
        commands.add("-jar");
        commands.add(jar);
        commands.add("-configuration");
        commands.add(configPath);
        commands.add("-data");
        commands.add(properties.getWorkspace());

        // 3. 启动 jar
        ProcessBuilder builder = new ProcessBuilder(commands);

        Process javaLspProc = builder.start();
        localCache.put(Constants.CACHED_JAVA_LSP_PROC, javaLspProc);
        log.info("JavaLspServer 启动成功, PID: {}", javaLspProc.pid());
    }

    private String getLauncher() {
        if (properties.getHome() == null) {
            return null;
        }
        return FileUtils.listFiles(Path.of(properties.getHome(), "plugins").toFile(),
                FileFilterUtils.and(FileFilterUtils.prefixFileFilter("org.eclipse.equinox.launcher_"), FileFilterUtils.suffixFileFilter("jar")),
                null).stream().map(File::getAbsolutePath).findFirst().orElse(null);
    }

    private String getConfigPath() {
        String dirName = OS.contains("windows") ? "config_win" : "config_linux";
        String[] files =  new File(properties.getHome()).list(FileFilterUtils.nameFileFilter(dirName));
        if (files != null && files.length > 0) {
            return Path.of(properties.getHome(), files[0]).toString();
        }

        return null;
    }
}
