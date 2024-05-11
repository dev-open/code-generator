package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GitlabCreateGroupResponseDto {

    /** 群组 ID */
    private Integer id;

    /** web 地址 */
    @JsonAlias("web_url")
    private String webUrl;

    /** 群组名称 */
    private String name;

    /** 群组路径 */
    private String path;

    /** 描述 */
    private String description;

    /** 创建嵌套群组的父群组 ID */
    @JsonAlias("parent_id")
    private Integer parentId;
}
