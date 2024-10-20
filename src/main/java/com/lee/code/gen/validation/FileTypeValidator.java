package com.lee.code.gen.validation;

import com.lee.code.gen.validation.constraints.FileType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 文件类型检查
 */
public class FileTypeValidator implements ConstraintValidator<FileType, Object> {

    private String[] extensions;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (extensions.length == 0) {
            return true;
        }

        if (value instanceof MultipartFile multipartFile) {
            String fileName = multipartFile.getOriginalFilename();
            return checkExtension(fileName);
        }

        if (value instanceof String fileName) {
            return checkExtension(fileName);
        }
        return true;
    }

    @Override
    public void initialize(FileType fileType) {
        extensions = fileType.extensions();
    }

    private boolean checkExtension(String fileName) {
        String extension =  FilenameUtils.getExtension(fileName);
        if (StringUtils.isNotEmpty(extension)) {
            var ret = Arrays.stream(extensions).filter(extension::equals).findAny();
            return ret.isPresent();
        }
        return false;
    }
}
