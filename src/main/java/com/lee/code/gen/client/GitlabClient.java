package com.lee.code.gen.client;

import com.lee.code.gen.config.GitlabFeignClientConfig;
import com.lee.code.gen.dto.gitlab.GitlabProjResponseDto;
import com.lee.code.gen.dto.gitlab.*;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "gitlab", configuration = GitlabFeignClientConfig.class)
@Retryable(maxAttempts = 5,
        backoff = @Backoff(delay = 500L, maxDelay = 20000L, multiplier = 1.5),
        retryFor = RetryableException.class)
public interface GitlabClient {

    @PostMapping("/projects")
    GitlabProjResponseDto createProj(@RequestBody GitlabCreateProjRequestDto request);

    @DeleteMapping("/projects/{id}")
    void deleteProj(@RequestBody GitlabDeleteProjRequestDto request, @PathVariable Integer id);

    @PostMapping(value = "/projects/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    GitlabImportProjResponseDto importProj(GitlabImportProjRequestDto request);

    @PostMapping("/projects/{id}/repository/files/{filePath}")
    GitlabCreateNewFileResponseDto createNewFile(@RequestBody GitlabCreateNewFileRequestDto request, @PathVariable Integer id, @PathVariable String filePath);

    @PutMapping("/projects/{id}/repository/files/{filePath}")
    GitlabUpdateFileResponseDto updateFile(@RequestBody GitlabUpdateFileRequestDto request, @PathVariable Integer id, @PathVariable String filePath);

    @DeleteMapping("/projects/{id}/repository/files/{filePath}")
    void deleteFile(@RequestBody GitlabDeleteFileRequestDto request, @PathVariable Integer id, @PathVariable String filePath);

    @GetMapping("/projects/{id}/repository/files/{filePath}")
    GitlabGetFileResponseDto getFile(@PathVariable Integer id, @PathVariable String filePath, @RequestParam String ref);

    @PostMapping("/groups")
    GitlabCreateGroupResponseDto createGroup(@RequestBody GitlabCreateGroupRequestDto request);

    @DeleteMapping("/groups/{id}")
    void deleteGroup(@RequestBody GitlabDeleteGroupRequestDto request, @PathVariable Integer id);

    @GetMapping("/groups/{id}/projects")
    FeignResponse<List<GitlabProjResponseDto>> listGroupProjs(@PathVariable Integer id, @RequestParam(required = false) String search, @RequestParam(name = "order_by", required = false) String orderBy,
                            @RequestParam(required = false) Integer page, @RequestParam(name = "per_page", required = false) Integer perPage);
    @GetMapping("/groups")
    FeignResponse<List<GitlabGroupResponseDto>> listGroups(@RequestParam(required = false) String search, @RequestParam(name = "order_by", required = false) String orderBy);

    @GetMapping("/users/{id}")
    GitlabGetUserInfoResponseDto getUserInfo(@PathVariable Integer id);
}
