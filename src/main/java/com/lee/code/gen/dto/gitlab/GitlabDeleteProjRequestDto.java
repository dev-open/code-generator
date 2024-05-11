package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitlabDeleteProjRequestDto {

    /** 永久删除 */
    @JsonProperty("permanently_remove")
    private Boolean permanentlyRemove;
}
