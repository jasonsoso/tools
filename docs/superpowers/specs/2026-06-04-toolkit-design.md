# 工具包设计规格说明

## 概述

开发一个在线工具包，包含两个工具：**Markdown 在线编辑器** 和 **JSON 在线格式化器**。前后端分离架构，支持用户认证、数学公式、表格编辑、目录大纲和文档导出。本期聚焦单用户核心功能，架构上为多用户预留扩展空间。文件上传不在本期范围。

## 技术栈

| 层 | 技术 | 版本 |
|---|---|---|
| 前端框架 | Vue 3 + TypeScript + Vite | 最新稳定版 |
| CSS | Tailwind CSS | 3.x |
| 状态管理 | Pinia | 2.x |
| Markdown 编辑器 | CodeMirror 6 | 最新 |
| Markdown 渲染 | markdown-it + highlight.js | 最新 |
| 数学公式 | KaTeX | 最新 |
| 后端框架 | Spring Boot 3 | 3.x |
| JDK | JDK 17 | 17 LTS |
| 认证 | Spring Security + JWT | - |
| 持久层 | MyBatis-Plus | 3.x |
| 数据库 | MySQL | 8.0 |

## 架构

```
浏览器 (Vue 3 SPA)
    ↕ REST API (JSON) + JWT Auth
Spring Boot 3 服务
    ↕ MyBatis-Plus
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
│       │   ├── request.ts          # axios 实例 + 拦截器
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
│       │   ├── TableEditorModal.vue
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
│       │   │   └── CorsConfig.java
│       │   ├── security/
│       │   │   ├── JwtTokenProvider.java
│       │   │   ├── JwtAuthFilter.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── MarkdownDoc.java
│       │   │   ├── JsonRecord.java
│       │   │   └── OperationLog.java
│       │   ├── mapper/
│       │   │   ├── UserMapper.java
│       │   │   ├── MarkdownDocMapper.java
│       │   │   ├── JsonRecordMapper.java
│       │   │   └── OperationLogMapper.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java
│       │   │   ├── MarkdownDocRepository.java
│       │   │   ├── JsonRecordRepository.java
│       │   │   └── OperationLogRepository.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── MarkdownService.java
│       │   │   ├── JsonService.java
│       │   │   └── LogService.java
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── MarkdownController.java
│       │   │   ├── JsonController.java
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
| `/markdown` | MarkdownEditor | 是 | Markdown 编辑器（新建） |
| `/markdown/:id` | MarkdownEditor | 是 | 打开已有文档 |
| `/json` | JsonFormatter | 是 | JSON 格式化器（新建） |
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
    │   ├── DocumentList（左侧文档列表）
    │   ├── MdToolbar（工具栏）
    │   ├── TableEditorModal（表格编辑器弹窗）
    │   ├── EditorPanel（代码编辑区）
    │   ├── MdPreview（右侧实时预览 + KaTeX 公式渲染）
    │   ├── MdOutline（目录大纲面板）
    │   └── ExportMenu（导出下拉菜单）
    └── JsonFormatter
        ├── RecordList（左侧记录列表）
        ├── InputPanel（JSON 输入区）
        └── OutputPanel
            └── JsonTree（可折叠树形视图）
```

### 状态管理（Pinia Store）

- **useAuthStore** — 登录态、JWT token、自动登录（localStorage 持久化 token）
- **useMarkdownStore** — 当前文档、文档列表、大纲数据、保存/加载状态
- **useJsonStore** — 当前 JSON 记录、记录列表、格式化状态

### 用户认证流程

1. 用户注册 → bcrypt 加密密码 → 返回 JWT token → 自动登录
2. 用户登录 → 验证密码 → 返回 JWT token（有效期 7 天）
3. 前端 axios 拦截器：所有请求自动携带 `Authorization: Bearer <token>`
4. 后端 JwtAuthFilter：校验 token → 注入 SecurityContext
5. token 过期 → 401 → 前端自动跳转登录页
6. 架构预留：所有业务表含 `user_id` 字段，Service 层按用户隔离数据，后续自然支持多用户

### Markdown 编辑器功能

**编辑**
- 左右分栏：左侧 CodeMirror 6 编辑器（语法高亮、行号）+ 右侧 markdown-it 实时预览
- 工具栏按钮：加粗、斜体、H1、H2、H3、链接、图片（手动输入URL）、无序列表、有序列表、代码块、表格、公式
- 点击工具栏在 CodeMirror 光标处插入/包裹对应 Markdown 语法
- CodeMirror 6 扩展：markdown 语法高亮、行号显示、自动补全括号

**数学公式**
- KaTeX 渲染，支持行内公式 `$...$` 和块级公式 `$$...$$`

**表格编辑器**
- 工具栏点击"表格"→ 弹出 TableEditorModal
- 选择行列数（最大 10×10）→ 确认后在光标处插入 Markdown 表格语法

**目录大纲**
- MdOutline 组件实时解析 H1~H3 标题，生成层级大纲
- 点击大纲项定位到预览区对应标题

**导出**
- 导出 `.md` 文件（原始 Markdown 文本下载）
- 导出 HTML（markdown-it 渲染后的完整 HTML 页面下载）
- PDF 不在本期范围（可用浏览器打印功能替代）

**保存与同步**
- 手动保存按钮 + Ctrl+S 快捷键
- 保存时更新服务端文档，多设备登录同一账号加载即可同步

### JSON 格式化器功能

- 左右分栏：左侧输入 + 右侧结果
- 操作按钮：格式化（美化）、压缩、复制结果
- 输入验证：实时检测 JSON 格式错误并高亮
- 结果展示：树形可折叠视图 / 原始文本 切换
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
| user_id | BIGINT NOT NULL FK | 所属用户（预留多用户） |
| title | VARCHAR(255) NOT NULL | 文档标题 |
| content | LONGTEXT | 文档内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**json_records** — JSON 记录

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT NOT NULL FK | 所属用户（预留多用户） |
| name | VARCHAR(255) NOT NULL | 记录名称 |
| content | TEXT NOT NULL | JSON 内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**operation_logs** — 操作日志

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT FK | 操作用户（可空） |
| tool_type | VARCHAR(50) NOT NULL | 工具类型（markdown / json） |
| action | VARCHAR(100) NOT NULL | 操作类型（CREATE / UPDATE / DELETE） |
| detail | VARCHAR(500) | 详情（如 "创建文档：xxx"） |
| created_at | DATETIME NOT NULL | 操作时间 |

### REST API

所有 API 前缀为 `/api`。除认证接口外均需 JWT token。

**认证**
- `POST /api/auth/register` — 注册 `{ username, email, password }` → `{ token, userId, username }`
- `POST /api/auth/login` — 登录 `{ username, password }` → `{ token, userId, username }`

**Markdown 文档**
- `GET /api/markdown` — 当前用户文档列表
- `POST /api/markdown` — 创建文档 `{ title, content }` → 文档对象
- `GET /api/markdown/{id}` — 获取文档（校验归属）
- `PUT /api/markdown/{id}` — 更新文档 `{ title, content }`
- `DELETE /api/markdown/{id}` — 删除文档

**JSON 记录**
- `GET /api/json` — 当前用户记录列表
- `POST /api/json` — 创建记录 `{ name, content }`
- `GET /api/json/{id}` — 获取记录（校验归属）
- `PUT /api/json/{id}` — 更新记录 `{ name, content }`
- `DELETE /api/json/{id}` — 删除记录

**操作日志**
- `GET /api/logs?page=0&size=20` — 分页查询日志

### API 统一响应格式

```json
{ "code": 200, "message": "success", "data": { ... } }
```

| code | 含义 |
|---|---|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 404 | 资源不存在 |
| 500 | 服务端错误 |

### 分层职责

```
Controller → Service → Repository → Mapper (MyBatis-Plus) → DB
```

| 层 | 职责 | 依赖 |
|---|---|---|
| Controller | 接收 HTTP 请求，参数校验，调用 Service | Service |
| Service | 业务逻辑：校验、组装、事务、日志记录 | Repository |
| Repository | 封装 SQL 查询（LambdaQueryWrapper 等），提供语义化方法 | Mapper |
| Mapper | MyBatis-Plus BaseMapper，只声明表映射，不写代码 | — |

### 服务层

- **AuthService** — 注册、登录、密码 bcrypt 加密/验证
- **MarkdownDocRepository** — 文档查询封装（LambdaQueryWrapper），提供 findByUserId、findByIdAndUserId 等方法
- **JsonRecordRepository** — 记录查询封装，提供 findByUserId、findByIdAndUserId 等方法
- **UserRepository** — 用户查询封装，提供 findByUsername、findByEmail 等方法
- **OperationLogRepository** — 日志查询封装，提供 findByPage 分页方法
- **MarkdownService** — 文档业务逻辑（校验归属、组装数据、调用 Repository、自动记录日志）
- **JsonService** — 记录业务逻辑（JSON 校验、调用 Repository、自动记录日志）
- **LogService** — 日志分页查询逻辑

### 安全配置

- Spring Security + 无状态 JWT
- `/api/auth/**` 公开；其余 `/api/**` 需认证
- CorsConfig 允许 `http://localhost:5173` 跨域

## 错误处理

- 后端统一 `{ code, message, data }` 响应
- 前端 axios 响应拦截器：401 跳转登录页，其他错误 toast 提示
- JSON 非法输入在前端即时校验，不请求后端

## 测试策略

- **后端**：JUnit 5 + Mockito → Service 层单元测试；@WebMvcTest + MockMvc → Controller 集成测试
- **前端**：Vitest → store 和工具函数单元测试
- 暂不引入 E2E 测试

## 未纳入本期范围

- 文件上传（图片黏贴/拖拽上传）
- 暗色模式切换
- 国际化
- 实时协作（WebSocket）
- PDF 导出（可用浏览器打印替代）
- 密码重置/找回
- 第三方登录（OAuth）
