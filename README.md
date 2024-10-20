# Code Generator（代码生成器）

基于模板引擎（[**FreeMarker**](http://freemarker.foofun.cn/)）自定义模板文件和参数完成模板填充，使用 GitLab 作为存储服务。

### 主要技术

- Java 17
- Gradle
- SpringBoot 3
- Spring Security
- Open Feign
- Caffeine
- Monaco Editor

### 主要功能

- 模板管理

  创建模板工程，自定义 ftl 模板文件和模板参数，设置模板参数，由 FreeMarker 完成模板渲染，实现生成代码。

  - 基于Open Feign 实现对 GitLab 相关API的操作
  - 

- 模板工程的创建和删除
  - 实现版本的自动递增

- 上传模板文件和参数（json文件）
  - 基于json-schema-validator完成对模板参数的校验

- 基于MonacoEditor编辑器实现模板的在线编辑

- 支持模板参数以默认值填充
  - 将模板参数以json字符串的形式存储，支持自定义任意json格式的模板参数

- 根据设置的模板参数，填充模板后生成代码
  - 支持每个模板文件自定义生成代码的路径，路径支持参数填充
    - 自定义Validator实现对路径格式的校验
  - 支持预览生成的代码
    - 实现生成代码的树形结构展示

- 模板参数工具
  - 基于JDBC数据库连接的元信息，获取数据库及表的信息，转换为Java实体
    - 以json的形式展示
    - 支持导出为模板参数

- 基于SpringOAuthClient实现第三方Gitlab账户的认证

- 基于本地缓存Caffeine实现API访问频率限制（按IP）

- 基于GitlabCI实现持续集成

- 部署

  - docker镜像：`ccr.ccs.tencentyun.com/opensrcdevelop/code-generator:latest`

  - `docker-compose.yml`示例

    ```yaml
    services:
      code-generator:
        image: ccr.ccs.tencentyun.com/opensrcdevelop/code-generator:latest
        container_name: code-generator
        ports:
          - "8081:8080"
        restart: on-failure
        environment:
          CODE_GEN_GITLAB_URL: https://gitlab.com
          CODE_GEN_GITLAB_GROUP_NAME: code-gen-group
          CODE_GEN_GITLAB_PAT: *******************
          OAUTH2_GITLAB_CLIENT_ID: *******************
          OAUTH2_GITLAB_CLIENT_SECRET: *******************
          OAUTH2_GITLAB_REDIRECT_URI: http://127.0.0.1:8081/login/oauth2/code/gitlab
        volumes:
          - ./logs:/app/logs
    ```

  - 环境变量说明

    - CODE_GEN_GITLAB_URL：Gitlab访问地址
    - CODE_GEN_GITLAB_GROUP_NAME：Gitlab群组名
    - CODE_GEN_GITLAB_PAT：Gitlab个人访问令牌
      - scope：api
    - OAUTH2_GITLAB_CLIENT_ID：Gitlab应用ID
    - OAUTH2_GITLAB_CLIENT_SECRET：Gitlab应用密钥
    - OAUTH2_GITLAB_REDIRECT_URI：Gitlab应用回调地址
      - /login/oauth2/code/gitlab：固定地址

  - 执行命令：`docker compose up -d`

- 演示

  ![image01](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433356075-125e65e3-721b-4398-ae8a-f88696d8dfc9.png?x-oss-process=image%2Fformat%2Cwebp)

  ![image02](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433397157-32f3a665-25ea-4731-9907-bff91b9fb307.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image03](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433411192-522887d3-8053-4195-b6ec-b7626ed6e543.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image04](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433427333-e61f9bf2-aef2-4655-a4f9-fed2afbadd5f.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image05](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433444151-c809f824-360a-4c61-8a0f-3581f9685447.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image06](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433528207-293fef7c-19f6-4443-9ead-0cc1f2bb295d.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image07](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433544889-5477c64a-ef3a-480f-957b-a795b2e05391.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image08](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433574522-02439758-9464-4dbb-8230-2e91bd893d2c.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image09](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433614916-32277219-05a0-4b52-9ed3-a53def3f987f.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)

  ![image10](https://cdn.nlark.com/yuque/0/2024/png/27242554/1715433654515-9d05a475-45c8-49d0-840e-84fa1e2262cb.png?x-oss-process=image%2Fformat%2Cwebp%2Fresize%2Cw_1500%2Climit_0)
