package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GitlabProjResponseDto {

    /** 项目 ID */
    private Integer id;

    /** 项目名 */
    private String name;

    /** 项目路径 */
    private String path;

    /** 项目全路径 */
    @JsonAlias("path_with_namespace")
    private String pathWithNamespace;

    /** 项目描述 */
    private String description;

    /** 默认分支 */
    @JsonAlias("default_branch")
    private String defaultBranch;

    /** 主题 */
    private List<String> topics;

    /** 可见性 */
    private String visibility;

    /** web地址 */
    @JsonAlias("web_url")
    private String webUrl;

    /** 创建时间 */
    @JsonAlias("created_at")
    private LocalDateTime createdAt;
}
