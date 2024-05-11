package com.lee.code.gen;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.code.gen.common.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShutdownHook implements DisposableBean {

    private final Cache<String, Object> localCache;

    @Override
    public void destroy() throws Exception {
        Process javaLspProc = (Process) localCache.getIfPresent(Constants.CACHED_JAVA_LSP_PROC);
        if (javaLspProc != null) {
            javaLspProc.destroy();
            log.info("关闭 JavaLspServer, PID: {}, ExitCode: {}", javaLspProc.pid(), javaLspProc.waitFor());
        }
    }
}
