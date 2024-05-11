package com.lee.code.gen.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.code.gen.client.FeignResponse;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.common.RPage;
import com.lee.code.gen.common.ValidationError;
import com.lee.code.gen.core.entity.TemplateFileTree;
import com.lee.code.gen.dto.gitlab.GitlabGetFileResponseDto;
import com.lee.code.gen.exception.BizException;
import com.lee.code.gen.exception.JsonSchemaValidationException;
import com.lee.code.gen.exception.ServerException;
import com.lee.code.gen.filter.TraceFilter;
import com.networknt.schema.*;
import freemarker.template.Template;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class TemplateUtil {

    private static final String SCHEMA_PATH_PREFIX = "schema/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private TemplateUtil() {}

    /**
     * 填充模板
     *
     * @param template 模板
     * @param data 数据
     * @return 填充后的内容
     */
    public static String fillTemplate(String template, Map<String, Object> data) {
        if (data.isEmpty()) {
            return template;
        }

        try (StringReader reader = new StringReader(template);
             StringWriter writer = new StringWriter()) {
            // 填充
            Template processor = new Template(UUID.randomUUID().toString(), reader, null, StandardCharsets.UTF_8.name());
            processor.process(data, writer);

            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(e, Constants.TEMPLATE_FILL_FAIL_MSG_CODE);
        }
    }

    /**
     * 解码文件内容
     *
     * @param fileResponse 文件
     * @return 解码后的文件内容
     */
    public static String decodeFileContent(GitlabGetFileResponseDto fileResponse) {
        if (Constants.FILE_ENCODING_BASE64.equals(fileResponse.getEncoding())) {
            return new String(
                    Base64.getDecoder().decode(fileResponse.getContent()),
                    StandardCharsets.UTF_8);
        }

        return fileResponse.getContent();
    }

    /**
     * 设置分页信息
     *
     * @param response 响应
     * @param page 分页信息
     * @param <T> 泛型
     */
    public static <T> void setPageData(FeignResponse<?> response, RPage<T> page) {
        response.getHeader(Constants.GITLAB_HEADER_X_PREV_PAGE).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setPrevPage);
        response.getHeader(Constants.GITLAB_HEADER_X_TOTAL_PAGES).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setPages);
        response.getHeader(Constants.GITLAB_HEADER_X_TOTAL).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setTotal);
        response.getHeader(Constants.GITLAB_HEADER_X_PAGE).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setPage);
        response.getHeader(Constants.GITLAB_HEADER_X_NEXT_PAGE).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setNextPage);
        response.getHeader(Constants.GITLAB_HEADER_X_PER_PAGE).filter(StringUtils::hasText).map(Integer::valueOf).ifPresent(page::setPerPage);
    }

    /**
     * 将 UTC 时间转换为本地时间
     *
     * @param utc UTC 时间
     * @return 本地时间
     */
    public static LocalDateTime convertUTC2Local(LocalDateTime utc) {
        return utc.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 获取模板文件树
     *
     * @param paths 路径集合
     * @return 模板文件树
     */
    @SuppressWarnings("all")
    public static List<TemplateFileTree> getTemplateFileTree(List<String> paths) {
        if (ObjectUtils.isEmpty(paths)) {
            return Collections.emptyList();
        }

        // 1. 根据路径获取节点信息
        Map<String, Integer> nodes = new LinkedHashMap<>();
        AtomicInteger id = new AtomicInteger(1);
        paths.stream().map(Path::of).forEach(p -> {
            StringBuilder sb = new StringBuilder();
            p.forEach(sub -> {
                String tmpPath =  sb.append(sub.toString()).append(Constants.TEMPLATE_FILE_PATH_SEPARATOR).toString();
                if (!nodes.containsKey(tmpPath.substring(0, tmpPath.length() - 1))) {
                    nodes.put(tmpPath.substring(0, tmpPath.length() - 1), id.getAndIncrement());
                }
            });
        });

        // 1.1 设置父节点信息
        List<TemplateFileTree> trees = new ArrayList<>();
        nodes.forEach((k, v) -> {
            TemplateFileTree tree = new TemplateFileTree();
            tree.setId(v);

            String[] ks =  k.split(Constants.TEMPLATE_FILE_PATH_SEPARATOR);
            if (ks.length == 1) {
                tree.setParentId(0);
                tree.setName(ks[0]);
                tree.setPath(ks[0]);
            } else {
                String p = String.join(Constants.TEMPLATE_FILE_PATH_SEPARATOR, ks);
                tree.setName(ks[ks.length - 1]);
                tree.setPath(p);
                tree.setParentId(nodes.get(p.substring(0, p.lastIndexOf(Constants.TEMPLATE_FILE_PATH_SEPARATOR))));
            }
            trees.add(tree);
        });


        // 1.2 获取所有的根节点
        return trees.stream()
                .filter(t -> t.getParentId() == 0)
                .peek(t -> t.setChildren(getChildren(t, trees))).toList();
    }

    /**
     * Json Schema 校验
     *
     * @param jsonStr 待校验的 Json 字符串
     * @param schema Json Schema 文件名
     */
    public static void validJsonString(String jsonStr, String schema) {
        String key = SCHEMA_PATH_PREFIX + schema;
        JsonNode jsonNode = Try.of(() -> OBJECT_MAPPER.readTree(jsonStr)).getOrElseThrow(e -> {
            ValidationError error = new ValidationError();
            error.setErrorMsg("Invalid json string");
            return new JsonSchemaValidationException(List.of(error), key);
        });
        String schemaString = LocalCacheUtil.computeIfAbsent(key, TimeUnit.MINUTES.toSeconds(5), () ->
                Try.of(() -> IOUtils.toString(new ClassPathResource(SCHEMA_PATH_PREFIX + schema).getInputStream(), StandardCharsets.UTF_8)).getOrElseThrow(ServerException::new));

        JsonSchemaFactory jsonSchemaFactory =  JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .jsonMapper(OBJECT_MAPPER)
                .build();
        JsonSchema jsonSchema = jsonSchemaFactory.getSchema(schemaString);
        Set<ValidationMessage> validationMessages = jsonSchema.validate(jsonNode);
        if (!ObjectUtils.isEmpty(validationMessages)) {
            var errors = validationMessages.stream().map(x -> {
                ValidationError error = new ValidationError();
                error.setField(x.getInstanceLocation().toString());
                error.setErrorMsg(x.getMessage());
                return error;
            }).toList();
            throw new JsonSchemaValidationException(errors, key);
        }
    }

    /**
     * 递增版本号
     *
     * @param version 版本号
     * @return 版本号
     */
    public static String incrementVersion(String version) {
        if (version.contains(Constants.VERSION_SEPARATOR)) {
            int[] subVersions = Arrays.stream(version.split(Constants.VERSION_SEPARATOR_REGX)).mapToInt(Integer::valueOf).toArray();
            doIncrementVersion(subVersions, subVersions.length - 1);
            return Arrays.stream(subVersions).mapToObj(String::valueOf).collect(Collectors.joining(Constants.VERSION_SEPARATOR));
        }

        return version;
    }

    /**
     * 下划线转驼峰
     *
     * @param name 名
     * @return 驼峰命名
     */
    public static String underLineToCamel(String name) {
        if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
            return Constants.EMPTY_STRING;
        }
        String tmpName = name.toLowerCase();
        StringBuilder result = new StringBuilder();
        Arrays.stream(tmpName.split(Constants.UNDERLINE)).filter(org.apache.commons.lang3.StringUtils::isNotBlank).forEach(x -> {
            if (result.isEmpty()) {
                result.append(x);
            } else {
                result.append(org.apache.commons.lang3.StringUtils.capitalize(x));
            }
        });
        return result.toString();
    }

    /**
     * 向 MDC 中加入数据
     *
     * @param key 键
     * @param val 值
     */
    public static void putMDC(String key, String val) {
        MDC.put(key, val);
        TraceFilter.TTL_MDC.get().put(key, val);
    }

    /**
     * 获取登录用户的用户名
     *
     * @return 登录用户的用户名
     */
    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User oAuth2User  = (OAuth2User) authentication.getPrincipal();
        return oAuth2User.getName();
    }

    /**
     * 递归查询子节点
     */
    @SuppressWarnings("all")
    private static List<TemplateFileTree> getChildren(TemplateFileTree root, List<TemplateFileTree> all) {
        return all.stream().filter(t -> Objects.equals(t.getParentId(), root.getId()))
                .peek(t -> t.setChildren(getChildren(t, all))).toList();
    }

    /**
     * 递增版本号
     */
    private static void doIncrementVersion(int[] subVersions, int index) {
        if (index == 0) {
            subVersions[0]++;
        } else {
            if (++subVersions[index] > 10) {
                subVersions[index] = 0;
                doIncrementVersion(subVersions, index - 1);
            }
        }
    }
}
