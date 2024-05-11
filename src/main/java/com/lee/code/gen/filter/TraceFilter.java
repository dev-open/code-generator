package com.lee.code.gen.filter;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.filter.RestFilter;
import com.lee.code.gen.util.WebUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class TraceFilter extends RestFilter {

    public static final TransmittableThreadLocal<Map<String, String>> TTL_MDC = new TransmittableThreadLocal<>() {

        /**
         * 在多线程数据传递的时候，将数据复制给子线程 MDC
         */
        @Override
        protected void beforeExecute() {
            final Map<String, String> map  = get();
            map.forEach(MDC::put);
        }

        @Override
        protected void afterExecute() {
            MDC.clear();
        }

        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    @Override
    protected void doSubFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = UUID.randomUUID().toString();
            String remoteIP = WebUtil.getRemoteIP(request);
            response.addHeader(Constants.CODE_GEN_HEADER_X_REQUEST_ID, traceId);
            MDC.put(Constants.MDC_TRACE_ID, traceId);
            MDC.put(Constants.MDC_REMOTE_IP, remoteIP);
            TTL_MDC.get().put(Constants.MDC_TRACE_ID, traceId);
            TTL_MDC.get().put(Constants.MDC_REMOTE_IP, remoteIP);
            log.info("Generated TraceId: {}", TTL_MDC.get().get(Constants.MDC_TRACE_ID));
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
            TTL_MDC.get().clear();
            TTL_MDC.remove();
        }
    }
}
