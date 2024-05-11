package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GitlabGetUserInfoResponseDto {

    /** ID */
    private String id;

    /** 用户名 */
    private String username;

    /** 邮箱 */
    private String email;

    /** 状态 */
    private String state;

    /** Web 地址 */
    @JsonAlias("web_url")
    private String webUrl;
}
