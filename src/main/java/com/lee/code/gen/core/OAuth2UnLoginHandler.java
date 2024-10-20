package com.lee.code.gen.core;

import com.lee.code.gen.common.CodeEnum;
import com.lee.code.gen.common.R;
import com.lee.code.gen.util.SpringContextUtil;
import com.lee.code.gen.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.io.IOException;

public class OAuth2UnLoginHandler extends LoginUrlAuthenticationEntryPoint {

    public OAuth2UnLoginHandler() {
        super("/login");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (request.getRequestURI().startsWith(SpringContextUtil.getProperty("spring.controller.path-prefix"))) {
            WebUtil.sendResponse(R.optFail(CodeEnum.RCD20006.getCode()), HttpStatus.UNAUTHORIZED);
        } else {
            super.commence(request, response, authException);
        }
    }
}
