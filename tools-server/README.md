# tools-server

在线工具包后端服务，基于 Spring Boot 3 构建，提供 JWT 认证与 Markdown/JSON 工具的 REST API。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.0 |
| 语言 | Java | 17 |
| 安全 | Spring Security + JWT (jjwt) | 0.12.3 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL 8（生产）/ H2（开发/测试） | — |
| 工具 | Lombok, MapStruct | 1.5.5 |
| 测试 | JUnit 5, Mockito, Spring Security Test | — |

## 项目架构

```
tools-server/src/main/java/com/tools/
├── common/            # 通用模块 — 统一响应、异常、错误码
│   ├── ApiResponse          # 统一 JSON 响应包装 {code, message, data}
│   ├── BusinessException    # 业务异常（→ GlobalExceptionHandler 捕获）
│   └── ErrorCode            # 按 HTTP 语义分类的错误码枚举
│
├── config/            # Spring 配置
│   ├── SecurityConfig       # Spring Security 核心配置（无状态 JWT 方案）
│   ├── CorsConfig           # CORS 跨域规则
│   ├── GlobalExceptionHandler   # 全局异常 → JSON 响应
│   ├── MybatisPlusConfig    # MyBatis-Plus 分页插件
│   └── MyMetaObjectHandler  # 自动填充 createdAt / updatedAt
│
├── security/          # 认证与授权
│   ├── JwtTokenProvider     # JWT 生成、解析、验证
│   ├── JwtAuthFilter        # OncePerRequestFilter — 提取 Token → 注入 SecurityContext
│   ├── SecurityUtils        # 从 SecurityContext 获取当前用户 ID
│   └── UserDetailsServiceImpl   # 从数据库加载用户 → Spring Security UserDetails
│
├── controller/        # REST 控制器（薄层，仅做参数接收和响应包装）
│   ├── AuthController       # POST /api/auth/register, /api/auth/login
│   ├── MarkdownController   # CRUD /api/markdown
│   ├── JsonController       # CRUD /api/json
│   └── LogController        # GET /api/logs（分页）
│
├── service/           # 业务逻辑层
│   ├── AuthService          # 注册（唯一性校验 + BCrypt 加密） / 登录（密码比对）
│   ├── MarkdownService      # 文档 CRUD + 操作日志记录
│   ├── JsonService          # 记录 CRUD + JSON 格式校验 + 记录日志
│   └── LogService           # 操作日志分页查询
│
├── repository/        # 数据访问封装层（封装 MyBatis-Plus Mapper）
│   ├── UserRepository
│   ├── MarkdownDocRepository
│   ├── JsonRecordRepository
│   └── OperationLogRepository
│
├── mapper/            # MyBatis-Plus Mapper 接口
│   └── UserMapper, MarkdownDocMapper, JsonRecordMapper, OperationLogMapper
│
├── entity/            # 数据库实体（@TableName）
│   └── User, MarkdownDoc, JsonRecord, OperationLog
│
└── vo/                # 视图对象
    ├── req/            # 请求 VO（含 Jakarta Validation 注解）
    │   └── LoginReqVO, RegisterReqVO, MarkdownDocReqVO, JsonRecordReqVO
    ├── resp/           # 响应 VO
    │   └── LoginRespVO, MarkdownDocRespVO, JsonRecordRespVO, OperationLogRespVO
    └── converter/      # MapStruct 转换器（Entity ↔ VO）
```

## 配置说明

### 多环境

| Profile | 数据库 | 端口 | 配置文件 |
|---------|--------|------|----------|
| `dev`（默认激活） | H2 内存数据库 | 8081 | `application-dev.yml` |
| 生产 | MySQL 8 | 8080 | `application.yml` |

### JWT 配置

```yaml
jwt:
  secret: <256-bit-key>      # HMAC-SHA256 签名密钥，dev 和生产各自配置
  expiration: 604800000       # Token 过期时间（毫秒），默认 7 天
```

### 数据库初始化

dev 模式下 H2 数据库自动执行 `resources/db/schema-h2.sql` 建表。生产环境需手动执行对应 DDL。

## API 端点

> 除 `/api/auth/**` 外，所有接口需携带 `Authorization: Bearer <token>` 请求头。

### 认证

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/login` | 用户登录，返回 JWT | 否 |

### Markdown 文档

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/markdown` | 获取当前用户的文档列表（按更新时间倒序） |
| GET | `/api/markdown/{id}` | 获取单篇文档（含所有权校验） |
| POST | `/api/markdown` | 创建文档 |
| PUT | `/api/markdown/{id}` | 更新文档（支持部分更新） |
| DELETE | `/api/markdown/{id}` | 删除文档（不可逆） |

### JSON 记录

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/json` | 获取当前用户的记录列表（按更新时间倒序） |
| GET | `/api/json/{id}` | 获取单条记录（含所有权校验） |
| POST | `/api/json` | 创建记录（自动校验 JSON 格式） |
| PUT | `/api/json/{id}` | 更新记录（支持部分更新） |
| DELETE | `/api/json/{id}` | 删除记录（不可逆） |

### 操作日志

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/logs?page=0&size=20` | 分页查询操作日志（按时间倒序） |

### 统一响应格式

```json
// 成功
{"code": 200, "message": "success", "data": {...}}

// 失败
{"code": 401, "message": "用户名或密码错误", "data": null}
```

## 安全设计

- **无状态 JWT** — 不使用服务端 Session，每次请求独立验证 Token
- **BCrypt 密码加密** — 内置盐值，每次哈希不同，防彩虹表
- **用户数据隔离** — 所有 CRUD 操作均校验 `userId`，用户只能操作自己的数据
- **统一错误响应** — 401/403 返回 JSON（而非默认 HTML 页面），前端无需特殊解析
- **防止用户枚举** — 登录失败统一返回「用户名或密码错误」，不区分具体原因

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8（仅生产模式需要）

### 启动

```bash
# 开发模式（H2 内存数据库，端口 8081，无需 MySQL）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产模式（MySQL，端口 8080）
# 需先创建 tools_db 数据库
mvn spring-boot:run
```

### 测试

```bash
# 运行全部测试（使用 H2 内存数据库，无需外部依赖）
mvn test
```

## 数据库表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `users` | 用户表 | id, username, email, password_hash, created_at, updated_at |
| `markdown_documents` | Markdown 文档 | id, user_id, title, content, created_at, updated_at |
| `json_records` | JSON 记录 | id, user_id, name, content, created_at, updated_at |
| `operation_logs` | 操作日志 | id, user_id, tool_type, action, detail, created_at |
