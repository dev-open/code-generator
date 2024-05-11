package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GitlabUpdateFileRequestDto {

    /** 分支 */
    private String branch = "main";

    /** 文件内容 */
    @NotEmpty
    private String content;

    /** 文件编码（base64、text（默认）） */
    private String encoding;

    /** 提交信息 */
    @JsonProperty("commit_message")
    @JsonAlias({"commitMessage"})
    @NotEmpty
    private String commitMessage;
}
