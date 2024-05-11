package com.lee.code.gen.dto;

import lombok.Data;

@Data
public class GetFileResponseDto {

    /** 文件名称 */
    private String fileName;

    /** 文件大小 */
    private Integer fileSize;

    /** 文件内容 */
    private String content;
}
