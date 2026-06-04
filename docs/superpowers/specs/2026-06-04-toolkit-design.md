# 工具包设计规格说明

## 概述

开发一个在线工具包，包含两个工具：**Markdown 在线编辑器** 和 **JSON 在线格式化器**。前后端分离架构，支持数据持久化和操作记录。

## 技术栈

| 层 | 技术 | 版本 |
|---|---|---|
| 前端框架 | Vue 3 + TypeScript + Vite | 最新稳定版 |
| CSS | Tailwind CSS | 3.x |
| 状态管理 | Pinia | 2.x |
| Markdown 渲染 | marked + highlight.js | 最新 |
| 后端框架 | Spring Boot 3 | 3.x |
| JDK | JDK 17 | 17 LTS |
| 持久层 | Spring Data JPA | - |
| 数据库 | MySQL | 8.0 |

## 架构

```
浏览器 (Vue 3 SPA)
    ↕ REST API (JSON)
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
│       │   ├── markdown.ts
│       │   └── json.ts
│       ├── api/
│       │   ├── markdown.ts
│       │   └── json.ts
│       ├── views/
│       │   ├── HomePage.vue
│       │   ├── MarkdownEditor.vue
│       │   └── JsonFormatter.vue
│       ├── components/
│       │   ├── NavBar.vue
│       │   ├── ToolCard.vue
│       │   ├── MdToolbar.vue
│       │   ├── MdPreview.vue
│       │   ├── DocumentList.vue
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
│       │   │   └── CorsConfig.java
│       │   ├── entity/
│       │   │   ├── MarkdownDoc.java
│       │   │   ├── JsonRecord.java
│       │   │   └── OperationLog.java
│       │   ├── repository/
│       │   │   ├── MarkdownDocRepo.java
│       │   │   ├── JsonRecordRepo.java
│       │   │   └── OperationLogRepo.java
│       │   ├── service/
│       │   │   ├── MarkdownService.java
│       │   │   ├── JsonService.java
│       │   │   └── LogService.java
│       │   ├── controller/
│       │   │   ├── MarkdownController.java
│       │   │   ├── JsonController.java
│       │   │   └── LogController.java
│       │   └── dto/
│       │       ├── MarkdownDocDto.java
│       │       └── JsonRecordDto.java
│       └── resources/
│           └── application.yml
└── docs/
```

## 前端设计

### 路由

| 路径 | 页面 | 说明 |
|---|---|---|
| `/` | HomePage | 工具导航首页，两张卡片 |
| `/markdown` | MarkdownEditor | Markdown 编辑器 |
| `/json` | JsonFormatter | JSON 格式化器 |

### 组件树

```
App.vue
├── NavBar（全局导航栏，有 Logo + 工具链接）
└── RouterView
    ├── HomePage
    │   └── ToolCard × 2（Markdown / JSON 入口卡片）
    ├── MarkdownEditor
    │   ├── DocumentList（左侧文档列表）
    │   ├── MdToolbar（编辑工具栏：加粗/斜体/标题/链接等）
    │   ├── EditorPanel（代码编辑区，textarea）
    │   └── MdPreview（右侧实时预览）
    └── JsonFormatter
        ├── RecordList（左侧记录列表）
        ├── InputPanel（JSON 输入区）
        └── OutputPanel
            └── JsonTree（可折叠树形视图）
```

### 状态管理（Pinia Store）

- **useMarkdownStore** — 当前文档、文档列表、加载/保存状态
- **useJsonStore** — 当前 JSON 记录、记录列表、格式化状态
- **useLogStore** — 操作日志（可选）

### Markdown 编辑器功能

- 左右分栏：左侧编辑区 + 右侧实时预览
- 工具栏按钮：加粗、斜体、H1、H2、H3、链接、图片、无序列表、有序列表、代码块
- 点击工具栏按钮在光标处插入/包裹对应 Markdown 语法
- 文档管理：新建（创建空白文档）、手动保存（点击保存按钮）、加载已有文档列表、删除文档
- 预览使用 marked 渲染 + highlight.js 代码高亮
- 不自动保存，用户需手动点击保存按钮

### JSON 格式化器功能

- 左右分栏：左侧输入 + 右侧结果
- 操作按钮：格式化（美化）、压缩、复制结果
- 输入验证：实时检测 JSON 格式错误并高亮提示
- 结果展示：树形可折叠视图 + 原始文本两种模式切换
- 记录管理：手动保存、加载已有记录、删除记录

## 后端设计

### 数据库表

**markdown_documents** — Markdown 文档

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| title | VARCHAR(255) NOT NULL | 文档标题 |
| content | TEXT | 文档内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**json_records** — JSON 记录

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| name | VARCHAR(255) NOT NULL | 记录名称 |
| content | TEXT NOT NULL | JSON 内容 |
| created_at | DATETIME NOT NULL | 创建时间 |
| updated_at | DATETIME NOT NULL | 更新时间 |

**operation_logs** — 操作日志

| 列 | 类型 | 说明 |
|---|---|---|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| tool_type | VARCHAR(50) NOT NULL | 工具类型（markdown/json） |
| action | VARCHAR(100) NOT NULL | 操作类型（CREATE/UPDATE/DELETE） |
| detail | VARCHAR(500) | 操作详情（如"创建文档：xxx"、"删除记录：yyy"） |
| created_at | DATETIME NOT NULL | 操作时间 |

### REST API

所有 API 前缀为 `/api`。

**Markdown 文档**
- `GET /api/markdown` — 文档列表
- `POST /api/markdown` — 创建文档
- `GET /api/markdown/{id}` — 获取文档
- `PUT /api/markdown/{id}` — 更新文档
- `DELETE /api/markdown/{id}` — 删除文档

**JSON 记录**
- `GET /api/json` — 记录列表
- `POST /api/json` — 创建记录
- `GET /api/json/{id}` — 获取记录
- `PUT /api/json/{id}` — 更新记录
- `DELETE /api/json/{id}` — 删除记录

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

- 成功：`code=200`，`data` 为业务数据
- 客户端错误：`code=4xx`，`data=null`
- 服务端错误：`code=500`，`data=null`

### 服务层

- **MarkdownService** — 文档 CRUD + 自动记录操作日志
- **JsonService** — 记录 CRUD + 自动记录操作日志
- **LogService** — 日志分页查询

### 跨域配置

CorsConfig 允许 `http://localhost:5173`（Vite 开发服务器）跨域访问。

## 错误处理

- 后端统一返回 `{ code, message, data }` 格式
- 前端 axios 拦截器统一处理错误提示
- JSON 格式化时非法输入在前端即时校验，不发送到后端

## 测试策略

- **后端**：Spring Boot Test + JUnit 5 对 Service 层做单元测试；@WebMvcTest 对 Controller 做集成测试
- **前端**：Vitest 对 store 和工具函数做单元测试
- 暂不引入 E2E 测试

## 未纳入本期范围

- 用户认证/登录
- Markdown 导出（PDF/HTML）
- 暗色模式切换
- 国际化
- 实时协作
