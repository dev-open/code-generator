package com.lee.code.gen.dto.gitlab;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class GitlabGetFileResponseDto {

    /** 文件名 */
    @JsonAlias("file_name")
    private String fileName;

    /** 文件路径 */
    @JsonAlias("file_path")
    private String filePath;

    /** 文件大小 */
    private Integer size;

    /** 文件编码（base64 或 text） */
    private String encoding;

    /** 文件内容 */
    private String content;

    /** SHA256 */
    @JsonAlias("content_sha256")
    private String contentSha256;

    /** 分支、标签或提交的名称 */
    private String ref;

    /** Blob Id */
    @JsonAlias("blob_id")
    private String blobId;
}
