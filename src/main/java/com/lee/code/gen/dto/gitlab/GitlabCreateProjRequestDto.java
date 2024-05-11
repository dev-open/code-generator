package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GitlabCreateProjRequestDto {

    /** 新项目的名称 */
    @NotEmpty
    private String name;

    /** 新项目的名称 */
    private String path;

    /** 描述 */
    private String description;

    /** 启用 Auto DevOps */
    @JsonProperty("auto_devops_enabled")
    @JsonAlias({"autoDevopsEnabled"})
    private Boolean autoDevopsEnabled = Boolean.FALSE;

    /** 默认分支  */
    @JsonProperty("default_branch")
    @JsonAlias({"defaultBranch"})
    private String defaultBranch = "main";

    /** 新项目的命名空间 （默认为当前用户的命名空间） */
    @JsonProperty("namespace_id")
    @JsonAlias({"namespaceId"})
    private Integer namespaceId;
}
