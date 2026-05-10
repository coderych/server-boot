# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

`com.coderych:server` — 基于 Spring Boot 4.0 的多模块企业级应用框架，采用 Java 25。

## 构建与运行

项目使用 Maven Wrapper（`.mvn`目录已存在）：

```bash
# 使用 Maven Wrapper（推荐）
./mvnw clean install          # 构建所有模块
./mvnw clean test             # 运行测试
./mvnw clean package -pl <module>  # 构建单个模块
./mvnw spring-boot:run -pl server-start  # 启动应用

# 或使用系统 Maven
mvn clean install
mvn clean test
mvn clean package -pl <module>
mvn spring-boot:run -pl server-start
```

### 环境切换（Maven Profiles）

项目通过 Maven Profile 切换环境，激活对应的 `application-{env}.yml`：

```bash
mvn clean package -P dev    # 开发环境（默认）
mvn clean package -P test   # 测试环境
mvn clean package -P prod   # 生产环境
```

## 架构

根 POM（`com.coderych:server:1.0-SNAPSHOT`，Java 25）：

```
server
├── server-commons              # 共享库聚合模块
│   ├── commons-all             # 一站式依赖引入（聚合所有 commons 子模块）
│   ├── commons-core            # 核心工具、基础实体、共享常量
│   ├── commons-web             # Web 层（控制器、过滤器、响应封装）
│   ├── commons-test            # 测试基础设施
│   ├── commons-cache           # 缓存（Redis / Spring Cache）
│   ├── commons-sa-token        # Sa-Token 认证授权
│   ├── commons-mybatis-flex    # MyBatis-Flex 数据库访问
│   ├── commons-log             # 日志/审计
│   ├── commons-oss             # 对象存储（S3、MinIO 等）
│   └── commons-job             # 定时任务
├── server-start                # 应用启动模块（@SpringBootApplication）
└── server-module-system        # 系统模块（用户、角色、权限）
```

## 技术栈

- **Spring Boot 4.0.3** — 应用框架
- **MyBatis-Flex 1.11.6** — ORM / 数据库访问
- **Sa-Token 1.45.0** — 认证授权
- **Redisson 4.3.0** — Redis 客户端
- **Hutool 5.8.43** — 工具库
- **Lombok 1.18.42** — 代码生成
- **MapStruct Plus 1.5.0** — 对象映射
- **Spring AI 2.0.0-M4** — AI 集成
- **基础包名：** `com.coderych.server.*`

## 关键设计模式

### 四层模型（Entity / Query / Form / DTO）

业务模块采用四层模型分离：
- **Entity** — 继承 `BaseEntity`（含 id、creator、createTime、updater、updateTime、deleted、version 字段）
- **Query** — 查询条件对象（用于列表/分页查询参数绑定）
- **Form** — 表单对象（用于新增/修改请求体，带 `@Valid` 校验）
- **DTO** — 数据传输对象（返回给前端，通过 MapStruct Plus 自动映射）

### 通用 CRUD 基类

`commons-mybatis-flex` 提供开箱即用的 CRUD 脚手架：
- `BaseController<S, E, Q, F, D>` — 标准 REST 接口（page/list/getById/save/update/delete）
- `BaseService<E, Q, F, D>` — 业务服务接口
- `BaseServiceImpl` — 服务实现基类
- `@CrudApi` 注解 + `CrudApiAspect` — 控制哪些 CRUD 接口开放访问

### 自动配置（Spring Boot AutoConfiguration）

每个 commons 模块都包含 `autoconfigure` 包：
- `*AutoConfiguration.java` — 使用 `@AutoConfiguration` 注解
- `*Properties.java` — 使用 `@ConfigurationProperties` 注解，前缀为 `commons.{模块名}`

配置元数据通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。

各子功能可通过配置独立开关，格式为 `commons.{module}.{feature}.enabled=true/false`（默认 true）。

### 注解驱动 AOP

模块通过自定义注解 + 切面提供声明式能力：
- `@AutoLog` — 自动记录操作日志（`commons-log`）
- `@Lock` — 分布式锁（`commons-cache`，基于 SpEL 解析 key）
- `@CrudApi` — CRUD 接口访问控制（`commons-mybatis-flex`）

### 核心工具类（commons-core）

模块初始化时通过 `SmartInitializingSingleton` 注入静态工具：
- `JSON` — Jackson JSON 操作（需 `ObjectProvider<JsonMapper>`）
- `BEAN` — MapStruct Plus 对象映射（需 `ObjectProvider<Converter>`）
- `HttpUtils` — RestClient HTTP 请求（需 `ObjectProvider<RestClient.Builder>`）
- `SpringUtils` — Spring 容器工具
- `LoginUser` — Sa-Token 登录用户工具（权限/角色检查，超级管理员自动放行）

### 版本管理

使用 CI-friendly 版本号（`${revision}`）+ `flatten-maven-plugin`：
- 根 POM 定义 `<revision>1.0-SNAPSHOT</revision>`
- 子模块使用 `<version>${revision}</version>`
- 构建时自动解析为实际版本号

### 全局依赖

所有模块自动继承：
- **Lombok**（provided scope）— 编译期生成 getter/setter
- **MapStruct Plus** — 编译期生成对象映射代码

### 模块聚合

`commons-all` 是一站式依赖模块，引入所有 commons 子模块。业务模块只需依赖 `commons-all` 即可获得全部基础能力。

## 测试约定

- 测试类命名：`*Tests.java`（复数，如 `RTests.java`、`QueryWrapperBuilderTests.java`）
- 测试方法命名：`methodNameShouldExpectedBehavior()`（camelCase + Should）
- 测试框架：JUnit 5 + Mockito
- 测试位于各模块的 `src/test/java` 下，包结构与主代码一致

## 编码原则

详见 [AGENTS.md](AGENTS.md) — 先思考再编码、简洁优先、精准修改、目标驱动。

## 行为规则

1. **语言**：始终使用中文进行回复。
2. **Git 提交确认**：每次回复结束后，必须询问用户是否需要将本次更改提交到 Git。
