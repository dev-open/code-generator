package com.lee.code.gen.core;

import com.lee.code.gen.util.LocalCacheUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SavedRequest savedRequest  = requestCache.getRequest(request, response);

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        String clientId =  oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(clientId, oAuth2User.getName());

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
        var expiresAt = accessToken.getExpiresAt();
        var issuedAt = accessToken.getIssuedAt();
        // 缓存 AccessToken RefreshToken
        if (expiresAt != null && issuedAt != null && refreshToken != null) {
            var interval = ChronoUnit.SECONDS.between(issuedAt, expiresAt);
            LocalCacheUtil.computeIfAbsent(accessToken.getTokenValue(), interval, refreshToken::getTokenValue);
            // TODO: AccessToken 自动续期
        }
        log.info("OAuth2-{} 用户{}成功登录", clientId, oAuth2User.getName());
        if (savedRequest != null) {
            // 重定向
            getRedirectStrategy().sendRedirect(request, response, savedRequest.getRedirectUrl());
        } else {
            getRedirectStrategy().sendRedirect(request, response, "/");
        }
    }
}
