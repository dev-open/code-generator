package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GitlabImportProjRequestDto {

    /** 导入的项目名称，默认为项目的路径 */
    private String name;

    /** 上传的文件 */
    @JsonIgnore
    private MultipartFile file;

    /** 项目的名称和路径 */
    @NotEmpty
    private String path;

    /** 是否覆盖 */
    private Boolean overwrite = Boolean.TRUE;
}
