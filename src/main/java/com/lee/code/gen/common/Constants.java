package com.lee.code.gen.common;

/**
 * 常量定义
 */
public class Constants {

    private Constants () {}

    public static final String REST_RESPONSE_ATTR = "REST_RESPONSE_ATTR";
    public static final String FILE_ENCODING_BASE64 = "base64";
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};
    public static final String EMPTY_STRING = "";
    public static final String UNDERLINE = "_";
    public static final String CONFIG_FILE_PATH = "config.json";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE_DOWNLOAD = "application/octet-stream; charset=UTF-8";
    public static final String CACHED_GROUP_ID = "groupId";
    public static final String CACHED_JAVA_LSP_PROC = "javaLspProc";
    public static final String LOCAL_DATETIME_FORMAT_WITHOUT_SSS = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_DATETIME_FORMAT_WITH_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DEFAULT_REF_MAIN = "main";
    public static final String TEMPLATE_FILE_PATH_SEPARATOR = "/";
    public static final String VERSION_SEPARATOR = ".";
    public static final String VERSION_SEPARATOR_REGX = "\\.";
    public static final String DEFAULT_VERSION = "0.0.0";
    public static final String MDC_TRACE_ID = "TraceId";
    public static final String MDC_REMOTE_IP = "RemoteIP";
    public static final String MDC_USERNAME = "username";

    public static final Integer TEMPLATE_FILL_FAIL_MSG_CODE = 40001;
    public static final Integer CREATE_TEMPLATE_PROJ_FAIL_MSG_CODE = 40002;
    public static final Integer CREATE_TEMPLATE_FAIL_MSG_CODE = 40003;
    public static final Integer GET_TEMPLATE_INFO_FAIL_MSG_CODE = 40004;
    public static final Integer GET_TEMPLATE_PARAMETER_FAIL_MSG_CODE = 40005;
    public static final Integer DOWNLOAD_TEMPLATE_FAIL_MSG_CODE = 40006;
    public static final Integer SET_TEMPLATE_PARAMETERS_FAIL_MSG_CODE = 40007;
    public static final Integer GET_TEMPLATE_CONFIG_INFO_FAIL_MSG_CODE = 40008;
    public static final Integer TEMPLATE_NOT_EXISTS_MSG_CODE = 40009;
    public static final Integer REMOVE_TEMPLATE_FAIL_MSG_CODE = 40010;
    public static final Integer TEMPLATE_PARAMETER_NOT_EXISTS_MSG_CODE = 40011;
    public static final Integer REMOVE_TEMPLATE_PARAMETER_FAIL_MSG_CODE = 40012;
    public static final Integer REMOVE_TEMPLATE_PROJ_FAIL_MSG_CODE = 40013;
    public static final Integer GET_DB_CONNECTION_ERROR_MSG_CODE  =40014;
    public static final Integer GET_TABLE_ENTITY_ACCESS_TOKEN_EXPIRED_MSG_CODE = 40015;
    public static final Integer PREVIEW_GENERATED_CODE_FAIL_MSG_CODE = 40016;

    public static final String GITLAB_HEADER_X_NEXT_PAGE = "x-next-page";
    public static final String GITLAB_HEADER_X_PAGE = "x-page";
    public static final String GITLAB_HEADER_X_PER_PAGE = "x-per-page";
    public static final String GITLAB_HEADER_X_PREV_PAGE = "x-prev-page";
    public static final String GITLAB_HEADER_X_TOTAL = "x-total";
    public static final String GITLAB_HEADER_X_TOTAL_PAGES = "x-total-pages";
    public static final String CODE_GEN_HEADER_X_REQUEST_ID = "x-request-id";

    public static final String IP_ADDR_UNKNOWN = "unknown";

    public static final String JSON_SCHEMA_TEMPLATE_PARAMETERS = "TemplateParameters.schema.json";

    public static final String TARGET_PATH_REGEX = "^(?!/)(?:/?(?:[-_a-zA-Z0-9]*\\$\\{[a-zA-Z0-9.]+\\}[-_a-zA-Z0-9]*)*[-_a-zA-Z0-9]*)+(?<!/)(?:.(?:[a-zA-Z]+))?$";
    public static final String TEMPLATE_FILENAME_REGX = "^[-_a-zA-Z0-9]+$";
    public static final String TEMPLATE_PARAM_REGX = "^[a-zA-Z0-9]+$";
}
