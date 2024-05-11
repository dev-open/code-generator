package com.lee.code.gen.lsp;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.code.gen.JavaLspApplicationRunner;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.util.SpringContextUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/java-lsp")
@Slf4j
public class JavaLspServerChannel {

    private Session session;
    private static BufferedWriter writer;
    private static BufferedReader reader;
    private static final Cache<String, Object> localCache = SpringContextUtil.getBean(Cache.class);

    static {
        Process javaLspProc = (Process) localCache.getIfPresent(Constants.CACHED_JAVA_LSP_PROC);
        if (javaLspProc != null) {
            writer = javaLspProc.outputWriter(StandardCharsets.UTF_8);
            reader = javaLspProc.inputReader(StandardCharsets.UTF_8);
        }
    }

    // 每一个 WS 连接都会初始化一个 Bean
    private static final CopyOnWriteArraySet<JavaLspServerChannel> channels =new CopyOnWriteArraySet<>();

    /**
     * 连接打开
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("[JavaLspServerWebSocket] 新的连接：id={}，连接总数：{}", session.getId(), channels.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(CloseReason closeReason) {
        channels.remove(this);
        log.info("[JavaLspServerWebSocket] 连接断开：id={}，reason={}，连接总数：{}", session.getId(), closeReason, channels.size());
    }

    /**
     * 错误处理
     */
    public void onError(Throwable error) {
        log.error("[JavaLspServerWebSocket] 连接异常：id={}，异常：{}", session.getId(), error.getMessage());
        log.error(error.getMessage(), error);
    }

    /**
     * 收到消息
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        synchronized (JavaLspApplicationRunner.class) {
            if (writer != null) {
                // 输出到 JavaLsp
                String lspMsg = "Content-Length: " + message.length() + "\r\n\r\n" + message + "\r\n";
                writer.write(lspMsg);
                log.info("sessionId: {}, lsp server receive: {}", session.getId(), lspMsg);
                writer.flush();

                // 获取 JavaLsp 输入
                StringBuilder result = new StringBuilder();
                while (reader.ready()) {
                    result.append((char) reader.read());
                }
                log.info("lsp server send: {}", result);
                handleMsg(result.toString()).stream().findFirst().ifPresent(m -> session.getAsyncRemote().sendText(m));
            }
        }
    }

    private List<String> handleMsg(String rawMsg) {
        return Arrays.stream(rawMsg.split("\\r\\n|\\r|\\n|Content-Length")).filter(m ->
                !Constants.EMPTY_STRING.equals(m) && m.startsWith("{") && m.endsWith("}") && !m.contains("logMessage")).distinct().toList();
    }
}
