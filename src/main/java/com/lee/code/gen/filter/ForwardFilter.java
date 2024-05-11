package com.lee.code.gen.filter;

import com.lee.code.gen.core.filter.RestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * 处理前端路由转发至目标
 */
@RequiredArgsConstructor
public class ForwardFilter extends RestFilter {

    private final String targetUrl;

    @Override
    protected void doSubFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.getRequestDispatcher(targetUrl).forward(request, response);
    }
}
