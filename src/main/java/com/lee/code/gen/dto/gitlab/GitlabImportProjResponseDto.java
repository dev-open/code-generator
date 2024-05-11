package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GitlabImportProjResponseDto {

    /** 项目id */
    private Integer id;

    /** 项目名 */
    private String name;

    /** 项目名和路径 */
    private String path;

    /** 导入状态 */
    @JsonAlias("import_status")
    private String importStatus;
}
