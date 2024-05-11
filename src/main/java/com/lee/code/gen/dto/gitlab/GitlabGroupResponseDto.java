package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GitlabGroupResponseDto {

    /** 群组 ID */
    private Integer id;

    /** 群组名 */
    private String name;

    /** 群组路径 */
    private String path;

    /** 全路径 */
    @JsonAlias("full_path")
    private String fullPath;

    /** 群组描述 */
    private String description;

    /** 群组可见性 */
    private String visibility;

    /** Web 地址 */
    @JsonProperty("web_url")
    private String webUrl;

    /** 父群组 ID */
    @JsonAlias("parent_id")
    private Integer parentId;

    /** 创建时间 */
    @JsonAlias("created_at")
    private LocalDateTime createdAt;
}
