package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GitlabDeleteFileRequestDto {

    /** 分支 */
    private String branch = "main";

    /** 提交信息 */
    @JsonProperty("commit_message")
    @JsonAlias({"commitMessage"})
    @NotEmpty
    private String commitMessage;
}
