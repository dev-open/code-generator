# Code Generator（代码生成器）

※个人学习项目，仅作交流学习用

基于模板引擎（[FreeMarker](http://freemarker.foofun.cn/)）自定义模板文件和参数完成模板填充，结合Gitlab完成模板的存储和用户认证。

- 已完成的功能

  - 基于OpenFeign实现对Gitlab相关API的操作
    - 结合Spring-Retry实现超时重试机制
    - 自定义Decoder在反序列化响应对象的同时，存储ResponseHeader的信息
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

  - docker镜像：`ccr.ccs.tencentyun.com/dev-001/code-generator:latest`

  - `docker-compose.yml`示例

    ```yaml
    services:
      code-generator:
        image: ccr.ccs.tencentyun.com/dev-001/code-generator:latest
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

