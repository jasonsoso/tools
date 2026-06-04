# 工具包设计规格说明

## 概述

开发一个在线工具包，包含两个工具：**Markdown 在线编辑器** 和 **JSON 在线格式化器**。前后端分离架构，支持用户账号、多设备同步、图片上传、数学公式、表格编辑、目录大纲和文档导出。

## 技术栈

| 层 | 技术 | 版本 |
|---|---|---|
| 前端框架 | Vue 3 + TypeScript + Vite | 最新稳定版 |
| CSS | Tailwind CSS | 3.x |
| 状态管理 | Pinia | 2.x |
| Markdown 渲染 | marked + highlight.js | 最新 |
| 数学公式 | KaTeX | 最新 |
| 后端框架 | Spring Boot 3 | 3.x |
| JDK | JDK 17 | 17 LTS |
| 认证 | Spring Security + JWT | - |
| 持久层 | Spring Data JPA | - |
| 数据库 | MySQL | 8.0 |
| 文件存储 | 本地文件系统 | - |

## 架构

```
浏览器 (Vue 3 SPA)
    ↕ REST API (JSON) + JWT Auth
Spring Boot 3 服务
    ↕ JPA/Hibernate
MySQL 8
```

单个仓库前后端分目录：`tools-web/`（前端）和 `tools-server/`（后端）。

### 项目目录结构

```
tools/
├── tools-web/               # Vue 3 前端
│   ├── index.html
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   ├── package.json
│   └── src/
│       ├── main.ts
│       ├── App.vue
│       ├── router/index.ts
│       ├── stores/
│       │   ├── auth.ts
│       │   ├── markdown.ts
│       │   └── json.ts
│       ├── api/
│       │   ├── auth.ts
│       │   ├── markdown.ts
│       │   └── json.ts
│       ├── views/
│       │   ├── LoginPage.vue
│       │   ├── RegisterPage.vue
│       │   ├── HomePage.vue
│       │   ├── MarkdownEditor.vue
│       │   └── JsonFormatter.vue
│       ├── components/
│       │   ├── NavBar.vue
│       │   ├── ToolCard.vue
│       │   ├── MdToolbar.vue
│       │   ├── MdPreview.vue
│       │   ├── MdOutline.vue
│       │   ├── TableEditor.vue
│       │   ├── DocumentList.vue
│       │   ├── ExportMenu.vue
│       │   ├── JsonTree.vue
│       │   └── RecordList.vue
│       └── utils/
│           ├── markdown.ts
│           └── json.ts
├── tools-server/            # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/tools/
│       │   ├── ToolsApplication.java
│       │   ├── config/
│       │   │   ├── SecurityConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   └── FileUploadConfig.java
│       │   ├── security/
│       │   │   ├── JwtTokenProvider.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── MarkdownDoc.java
│       │   │   ├── JsonRecord.java
│       │   │   ├── UploadedFile.java
│       │   │   └── OperationLog.java
│       │   ├── repository/
│       │   │   ├── UserRepo.java
│       │   │   ├── MarkdownDocRepo.java
│       │   │   ├── JsonRecordRepo.java
│       │   │   ├── UploadedFileRepo.java
│       │   │   └── OperationLogRepo.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── MarkdownService.java
│       │   │   ├── JsonService.java
│       │   │   ├── FileService.java
│       │   │   └── LogService.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── MarkdownController.java
│       │   │   ├── JsonController.java
│       │   │   ├── FileController.java
│       │   │   └── LogController.java
│       │   └── dto/
│       │       ├── LoginRequest.java
│       │       ├── RegisterRequest.java
│       │       ├── LoginResponse.java
│       │       ├── MarkdownDocDto.java
│       │       └── JsonRecordDto.java
│       └── resources/
│           └── application.yml
└── docs/
```

## 前端设计

### 路由

| 路径 | 页面 | 认证要求 | 说明 |
|---|---|---|---|
| `/login` | LoginPage | 否 | 登录页 |
| `/register` | RegisterPage | 否 | 注册页 |
| `/` | HomePage | 是 | 工具导航首页，两张卡片 |
| `/markdown` | MarkdownEditor | 是 | Markdown 编辑器 |
| `/markdown/:id` | MarkdownEditor | 是 | 打开已有文档 |
| `/json` | JsonFormatter | 是 | JSON 格式化器 |
| `/json/:id` | JsonFormatter | 是 | 打开已有记录 |

### 组件树

```
App.vue
├── NavBar（Logo + 工具链接 + 用户信息/退出）
└── RouterView
    ├── LoginPage
    ├── RegisterPage
    ├── HomePage
    │   └── ToolCard × 2
    ├── MarkdownEditor
    │   ├── DocumentList（左侧文档列表，按用户过滤）
    │   ├── MdOutline（右侧目录大纲，解析标题自动生成）
    │   ├── MdToolbar（工具栏）
    │   ├── TableEditor（表格编辑器弹窗）
    │   ├── EditorPanel（代码编辑区，支持图片黏贴/拖拽）
    │   ├── MdPreview（右侧实时预览，含 KaTeX 公式渲染）
    │   └── ExportMenu（导出下拉菜单）
    └── JsonFormatter
        ├── RecordList
        ├── InputPanel
        └── OutputPanel
            └── JsonTree
```

### 状态管理（Pinia Store）

- **useAuthStore** — 用户登录态、token 管理、自动登录
- **useMarkdownStore** — 当前文档、文档列表、大纲数据、保存状态
- **useJsonStore** — 当前 JSON 记录、记录列表、格式化状态

### 用户认证流程

1. 用户注册 → 后端保存 bcrypt 加密密码 → 返回 JWT token
2. 用户登录 → 后端验证 → 返回 JWT token（有效期 7 天）
3. 前端所有 API 请求携带 `Authorization: Bearer <token>`
4. 后端 JwtAuthFilter 校验 token，注入 SecurityContext
5. 多设备同步：同一账号在不同设备登录后，自动看到云端最新数据

### Markdown 编辑器功能

**编辑**
- 左右分栏：左侧编辑区 + 右侧实时预览
- 工具栏按钮：加粗、斜体、H1~H3、链接、图片上传、无序列表、有序列表、代码块、表格、公式
- 点击工具栏按钮在光标处插入/包裹对应 Markdown 语法

**图片上传**
- 支持黏贴图片（Ctrl+V）和拖拽图片到编辑区
- 图片上传到后端 `/api/files/upload`，返回 URL
- 自动在光标处插入 `![alt](url)` 语法

**数学公式**
- 使用 KaTeX 渲染
- 行内公式：`$E=mc^2$`
- 块级公式：`$$\int_a^b f(x)dx$$`

**表格编辑器**
- 工具栏点击"表格"弹出表格编辑器弹窗
- 选择行列数（最多 10×10），点击确认插入 Markdown 表格语法

**目录大纲**
- 右侧 MdOutline 组件，实时解析文档中的 H1~H3 标题
- 显示为层级列表，点击跳转到预览区对应位置
- 大纲自动随编辑内容更新

**导出**
- 导出为 `.md` 文件（原始 Markdown 下载）
- 导出为 HTML（经过 marked 渲染的完整 HTML）
- 导出 PDF 暂不纳入本期（依赖浏览器打印功能即可覆盖）

**保存**
- 手动保存按钮 + 快捷键 Ctrl+S
- 保存时同步更新文档标题、内容、updated_at
- 多设备间通过服务端 CRUD 实现同步（加载即获取最新版本）

### JSON 格式化器功能

- 左右分栏：左侧输入 + 右侧结果
- 操作按钮：格式化（美化）、压缩、复制结果
- 输入验证：实时检测 JSON 格式错误并高亮提示
- 结果展示：树形可折叠视图 + 原始文本切换
- 记录管理：手动保存、加载已有记录、删除

## 后端设计

### 数据库表

**users** — 用户

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) NOT NULL UNIQUE | 用户名 |
| email | VARCHAR(100) NOT NULL UNIQUE | 邮箱 |
| password_hash | VARCHAR(255) NOT NULL | bcrypt 密码哈希 |
| created_at | DATETIME NOT NULL | 注册时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**markdown_documents** — Markdown 文档

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT NOT NULL FK | 所属用户 |
| title | VARCHAR(255) NOT NULL | 文档标题 |
| content | LONGTEXT | 文档内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**json_records** — JSON 记录

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT NOT NULL FK | 所属用户 |
| name | VARCHAR(255) NOT NULL | 记录名称 |
| content | TEXT NOT NULL | JSON 内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**uploaded_files** — 上传文件

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT NOT NULL FK | 上传用户 |
| original_name | VARCHAR(255) NOT NULL | 原始文件名 |
| stored_path | VARCHAR(500) NOT NULL | 存储路径 |
| file_size | BIGINT | 文件大小（字节） |
| url | VARCHAR(500) NOT NULL | 访问 URL |
| created_at | DATETIME NOT NULL | 上传时间 |

**operation_logs** — 操作日志

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT FK | 操作用户（可空） |
| tool_type | VARCHAR(50) NOT NULL | 工具类型（markdown/json） |
| action | VARCHAR(100) NOT NULL | 操作类型（CREATE/UPDATE/DELETE） |
| detail | VARCHAR(500) | 详情（如"创建文档：xxx"） |
| created_at | DATETIME NOT NULL | 操作时间 |

### REST API

所有 API 前缀为 `/api`。除认证接口外均需 JWT token。

**认证**
- `POST /api/auth/register` — 注册 `{ username, email, password }`
- `POST /api/auth/login` — 登录 `{ username, password }` → `{ token, userId, username }`

**Markdown 文档**
- `GET /api/markdown` — 当前用户文档列表
- `POST /api/markdown` — 创建文档
- `GET /api/markdown/{id}` — 获取文档（校验归属）
- `PUT /api/markdown/{id}` — 更新文档
- `DELETE /api/markdown/{id}` — 删除文档

**JSON 记录**
- `GET /api/json` — 当前用户记录列表
- `POST /api/json` — 创建记录
- `GET /api/json/{id}` — 获取记录（校验归属）
- `PUT /api/json/{id}` — 更新记录
- `DELETE /api/json/{id}` — 删除记录

**文件上传**
- `POST /api/files/upload` — 上传文件（multipart/form-data），返回 `{ url, originalName, fileSize }`
- `GET /api/files/{filename}` — 访问文件

**操作日志**
- `GET /api/logs?page=0&size=20` — 分页查询日志

### API 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| code | 含义 |
|---|---|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证（token 无效/过期） |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务端错误 |

### 服务层

- **AuthService** — 注册、登录、密码加密验证
- **MarkdownService** — 文档 CRUD（按 user_id 隔离）+ 自动记录日志
- **JsonService** — 记录 CRUD（按 user_id 隔离）+ 自动记录日志
- **FileService** — 文件上传/存储，限制 10MB/文件，仅允许图片类型
- **LogService** — 日志分页查询

### 安全配置

- Spring Security + 无状态 JWT
- 密码使用 bcrypt 加密存储
- 所有 `/api/**` 除 `/api/auth/**` 和 `/api/files/**`（GET）外需认证
- CorsConfig 允许 `http://localhost:5173` 跨域

## 错误处理

- 后端统一 `{ code, message, data }` 格式
- 前端 axios 拦截器：401 自动跳转登录页，其他错误 toast 提示
- JSON 格式化时非法输入在前端即时校验，不发送到后端

## 测试策略

- **后端**：JUnit 5 + Mockito 对 Service 层单元测试；@WebMvcTest + MockMvc 对 Controller 集成测试
- **前端**：Vitest 对 store 和工具函数做单元测试
- 暂不引入 E2E 测试

## 未纳入本期范围

- 暗色模式切换
- 国际化
- 实时协作（WebSocket）
- PDF 导出（可用浏览器打印功能替代）
- 密码重置/找回
- 第三方登录（OAuth）
