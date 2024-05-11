package com.lee.code.gen.service.impl;

import com.lee.code.gen.dto.GetLoginUserInfoResponseDto;
import com.lee.code.gen.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final String AVATAR_URL = "avatar_url";

    /**
     * 获取登录用户的信息
     *
     * @return 登录用户的信息
     */
    @Override
    public GetLoginUserInfoResponseDto getLoginUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        GetLoginUserInfoResponseDto response = new GetLoginUserInfoResponseDto();
        response.setUsername(oAuth2User.getName());
        response.setAvatarUrl(oAuth2User.getAttribute(AVATAR_URL));
        return response;
    }
}
