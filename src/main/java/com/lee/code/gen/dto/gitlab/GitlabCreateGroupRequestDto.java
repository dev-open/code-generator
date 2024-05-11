package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GitlabCreateGroupRequestDto {

    /** 群组名称 */
    @NotEmpty
    private String name;

    /** 群组路径 */
    @NotEmpty
    private String path;

    /** 群组内所有项目的 Auto DevOps 流水线 */
    @JsonProperty("auto_devops_enabled")
    @JsonAlias({"autoDevopsEnabled"})
    private Boolean autoDevopsEnabled = false;

    /** <a href="https://docs.gitlab.cn/jh/api/groups.html#default_branch_protection-%E9%80%89%E9%A1%B9">分支保护设置</a> */
    @JsonProperty("default_branch_protection")
    @JsonAlias({"defaultBranchProtection"})
    private Integer defaultBranchProtection = 1;

    /** 群组可见性：private、internal 或 public */
    private String visibility = "private";

    /** 群组描述 */
    private String description;

    /** 创建嵌套群组的父群组 ID */
    @JsonProperty("parent_id")
    @JsonAlias({"parentId"})
    private Integer parentId;

}
