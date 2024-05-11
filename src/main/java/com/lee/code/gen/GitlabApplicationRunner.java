package com.lee.code.gen;

import com.github.benmanes.caffeine.cache.Cache;
import com.lee.code.gen.client.GitlabClient;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.dto.gitlab.GitlabCreateGroupRequestDto;
import com.lee.code.gen.dto.gitlab.GitlabCreateGroupResponseDto;
import com.lee.code.gen.dto.gitlab.GitlabGroupResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class GitlabApplicationRunner implements ApplicationRunner {

    @Value("${code.gen.gitlab.group-name}")
    private String groupName;

    private static final Integer EXIT_CODE = -1;
    private static final String ORDER_KEY = "name";

    private final GitlabClient client;
    private final Cache<String, Object> localCache;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 1. 获取设定的 group-name
        if (!StringUtils.hasText(groupName)) {
            log.error("请设置正确的 Gitlab 群组名！");
            // 程序终止
            System.exit(EXIT_CODE);
        }

        // 2. 查询指定的群组是否存在
        List<GitlabGroupResponseDto> groups = client.listGroups(groupName, ORDER_KEY).getBody();
        Optional<GitlabGroupResponseDto> target = groups.stream().filter(g -> groupName.equals(g.getName())).findFirst();
        if (target.isEmpty()) {
            // 3. 创建群组
            GitlabCreateGroupRequestDto createGroup = new GitlabCreateGroupRequestDto();
            createGroup.setName(groupName);
            createGroup.setPath(groupName);
            GitlabCreateGroupResponseDto response = client.createGroup(createGroup);

            // 3.1 缓存 ID
            localCache.put(Constants.CACHED_GROUP_ID, response.getId());
            log.info("成功创建 Gitlab 群组（{}） - ID: {}, URL: {}", groupName, response.getId(), response.getWebUrl());
        } else {

            // 3.1 缓存 ID
            localCache.put(Constants.CACHED_GROUP_ID, target.get().getId());
            log.info("Gitlab 群组（{}）已存在 - ID: {}, URL: {}", groupName, target.get().getId(), target.get().getWebUrl());
        }
    }
}
