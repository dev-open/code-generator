package com.lee.code.gen.controller;

import com.lee.code.gen.annoation.RestResponse;
import com.lee.code.gen.client.GitlabClient;
import com.lee.code.gen.common.Constants;
import com.lee.code.gen.dto.GetFileResponseDto;
import com.lee.code.gen.dto.gitlab.GitlabProjResponseDto;
import com.lee.code.gen.dto.gitlab.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("gitlab")
@RestResponse
public class GitlabController {

    private final GitlabClient client;

    @PostMapping("/projects")
    public GitlabProjResponseDto createProj(@RequestBody @Valid GitlabCreateProjRequestDto request) {
        return client.createProj(request);
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProj(@PathVariable @NotNull Integer id) {
        client.deleteProj(new GitlabDeleteProjRequestDto(), id);
    }

    @PostMapping(value = "/projects/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GitlabImportProjResponseDto importProj(@Valid GitlabImportProjRequestDto request) {
        return client.importProj(request);
    }

    @PostMapping("/projects/{id}/repository/files/{filePath}")
    public GitlabCreateNewFileResponseDto createNewFile(@RequestBody @Valid GitlabCreateNewFileRequestDto request, @PathVariable @NotNull Integer id, @PathVariable @NotEmpty String filePath) {
        return client.createNewFile(request, id, filePath);
    }

    @PutMapping("/projects/{id}/repository/files/{filePath}")
    public GitlabUpdateFileResponseDto updateFile(@RequestBody @Valid GitlabUpdateFileRequestDto request, @PathVariable @NotNull Integer id, @PathVariable @NotEmpty String filePath) {
        return client.updateFile(request, id, filePath);
    }

    @DeleteMapping("/projects/{id}/repository/files/{filePath}")
    public void deleteFile(@RequestBody @Valid GitlabDeleteFileRequestDto request, @PathVariable @NotNull Integer id, @PathVariable @NotEmpty String filePath) {
        client.deleteFile(request, id, filePath);
    }

    @GetMapping("/projects/{id}/repository/files/{filePath}")
    public GetFileResponseDto getFile(@PathVariable @NotNull Integer id, @PathVariable @NotEmpty String filePath, @RequestParam(defaultValue = "main") String ref) {
        GitlabGetFileResponseDto file = client.getFile(id, filePath, ref);
        String encoding = file.getEncoding();
        String content = file.getContent();
        // Base64 解码
        if (Constants.FILE_ENCODING_BASE64.equals(encoding)) {
            content = new String(Base64.getDecoder().decode(content), StandardCharsets.UTF_8);
        }
        GetFileResponseDto response = new GetFileResponseDto();
        response.setFileName(file.getFileName());
        response.setFileSize(file.getSize());
        response.setContent(content);

        return response;
    }

    @PostMapping("/groups")
    public GitlabCreateGroupResponseDto createGroup(@RequestBody @Valid GitlabCreateGroupRequestDto request) {
        return client.createGroup(request);
    }

    @DeleteMapping("/groups/{id}")
    public void deleteGroup(@PathVariable @NotNull Integer id) {
        client.deleteGroup(new GitlabDeleteGroupRequestDto(), id);
    }

    @GetMapping("/groups/{id}/projects")
    public List<GitlabProjResponseDto> listGroupProjs(@PathVariable @NotNull Integer id, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer perPage) {
        return client.listGroupProjs(id, null, null, page, perPage).getBody();
    }

    @GetMapping("/groups")
    public List<GitlabGroupResponseDto> listGroups(@RequestParam(required = false) String search, @RequestParam(required = false) String orderBy) {
        return client.listGroups(search, orderBy).getBody();
    }

    @GetMapping("/users/{id}")
    public GitlabGetUserInfoResponseDto getUserInfo(@PathVariable Integer id) {
        return client.getUserInfo(id);
    }
}
