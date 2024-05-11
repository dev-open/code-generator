package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GitlabUpdateFileResponseDto {

    /** 文件路径 */
    @JsonAlias("file_path")
    private String filePath;

    /** 分支 */
    private String branch;
}
