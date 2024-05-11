package com.lee.code.gen.filter;

import com.lee.code.gen.common.Constants;
import com.lee.code.gen.core.filter.RestFilter;
import com.lee.code.gen.util.TemplateUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;

public class SubAuthenticationFilter extends RestFilter {

    @Override
    protected void doSubFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Authentication) {
            OAuth2User oAuth2User = oAuth2Authentication.getPrincipal();
            TemplateUtil.putMDC(Constants.MDC_USERNAME, oAuth2User.getName() + "(" + ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId() + ")");
        }
        filterChain.doFilter(request, response);
    }
}
