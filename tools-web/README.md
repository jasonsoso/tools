# tools-web

在线工具包前端，基于 Vue 3 + TypeScript 构建，提供 Markdown 编辑器和 JSON 格式化器。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API + `<script setup>`) | 3.5 |
| 语言 | TypeScript | 6.0 |
| 构建 | Vite | 8.0 |
| 样式 | Tailwind CSS | 4.3 |
| 状态管理 | Pinia | 3.0 |
| 路由 | Vue Router | 4.6 |
| 编辑器 | CodeMirror 6 | 6.43 |
| Markdown | markdown-it | 14.2 |
| 数学公式 | KaTeX | 0.17 |
| 测试 | Vitest + Vue Test Utils | 4.1 |

## 设计系统

遵循 **极简 · 高级 · 专业** 的 Apple 风格设计语言。

| 要素 | 规范 |
|------|------|
| 背景色 | `#FAFAFA` |
| 卡片 | 纯白 + 16px 圆角 + 柔和阴影 |
| 强调色 | 蓝紫渐变 `#6366f1 → #8b5cf6` |
| 字体 | Inter / SF Pro / PingFang SC / Microsoft YaHei |
| 圆角 | 卡片 16px · 输入框 12px · 按钮 10px |
| 阴影 | 大半径低透明度 (`rgba(0,0,0,.02~.06)`) |
| 玻璃态 | `backdrop-blur(20px)` 半透明白底 |

全局样式定义在 `src/style.css`，包含 `.card` / `.input-field` / `.btn-primary` / `.glass` 等可复用 class。

## 项目结构

```
tools-web/src/
├── api/               # Axios 封装 + 按模块拆分的 API 函数
│   ├── request             # Axios 实例（baseURL, 拦截器, Token 注入, 401 处理）
│   ├── auth                # 登录/注册
│   ├── markdown            # 文档 CRUD
│   └── json                # JSON 记录 CRUD
│
├── router/            # Vue Router 路由配置 + 导航守卫
│   └── index               # 路由定义 + beforeEach 认证检查
│
├── stores/            # Pinia 状态管理
│   ├── auth                # 用户认证状态（token, username, login/register/logout）
│   ├── markdown            # 文档列表 + 当前文档状态
│   └── json                # JSON 记录列表 + 当前记录状态
│
├── views/             # 页面组件
│   ├── LoginPage           # 登录页 — 卡片表单
│   ├── RegisterPage        # 注册页 — 卡片表单
│   ├── HomePage            # 首页 — Hero + 工具卡片入口
│   ├── MarkdownEditor      # Markdown 编辑器 — 侧边栏 + 编辑/预览/大纲三栏
│   └── JsonFormatter       # JSON 格式化器 — 侧边栏 + 输入/输出双栏
│
├── components/        # 通用组件
│   ├── NavBar              # 玻璃态导航栏
│   ├── ToolCard            # 工具卡片（SVG 图标 + 箭头微交互）
│   ├── DocumentList        # Markdown 文档侧边栏列表
│   ├── RecordList          # JSON 记录侧边栏列表
│   ├── MdToolbar           # Markdown 编辑工具栏（分组按钮）
│   ├── MdPreview           # Markdown 渲染预览
│   ├── MdOutline           # 文档目录大纲
│   ├── ExportMenu          # 导出下拉菜单（.md / HTML）
│   ├── TableEditorModal    # 表格插入弹窗
│   ├── JsonTree            # JSON 树形视图
│   └── TreeNodeItem        # JSON 树节点（递归渲染）
│
├── utils/             # 工具函数
│   ├── markdown            # markdown-it 渲染 + KaTeX + 目录提取 + 导出
│   └── json                # JSON 格式化/压缩/校验/剪贴板
│
├── style.css           # 全局样式 + 设计系统
├── App.vue             # 根组件（#FAFAFA 背景 + NavBar + router-view）
└── main.ts             # 入口（createApp, Pinia, Router）
```

## 页面与功能

### 首页

- Hero 区展示工具名称和标语
- 两张卡片分别跳转 Markdown 编辑器和 JSON 格式化器
- 卡片 hover 时箭头右移、边框高亮

### Markdown 编辑器

- **三栏布局**：文档列表 → 编辑/预览 → 目录大纲
- **CodeMirror 6** 编辑器，支持 Markdown 语法高亮
- **实时预览**：markdown-it 渲染，KaTeX 数学公式，GFM 表格
- **工具栏**：加粗、斜体、标题、链接、图片、列表、代码块、公式、表格
- **表格弹窗**：选择行列数后自动插入 Markdown 表格
- **目录大纲**：提取标题生成可点击跳转的导航
- **导出**：支持下载 .md 和 HTML 格式
- **快捷键**：`Ctrl+S` 保存

### JSON 格式化器

- **双栏布局**：输入 ↔ 结果
- **格式化**：美化 JSON 缩进
- **压缩**：移除空格和换行
- **复制**：结果一键复制到剪贴板
- **树形视图**：可展开/折叠的 JSON 树
- **实时校验**：输入时即时提示 JSON 格式是否合法

### 登录 / 注册

- 卡片式表单，极简设计
- 注册支持用户名 + 邮箱 + 密码
- 登录失败统一错误提示
- 认证后自动跳转回来源页面

## 快速开始

### 环境要求

- Node.js >= 18

### 启动

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev
```

> 开发时需要后端服务运行在 `http://localhost:8081`（dev 模式），否则 API 请求会失败。

### 构建

```bash
npm run build
```

产物输出到 `dist/`，可直接部署到任意静态服务。

### 测试

```bash
npx vitest run
```

测试覆盖：Auth Store、JSON 工具函数、Markdown 工具函数。

## 路由设计

| 路径 | 页面 | 认证要求 |
|------|------|----------|
| `/` | 首页 | 否 |
| `/login` | 登录 | 否 |
| `/register` | 注册 | 否 |
| `/markdown` | Markdown 编辑器（新建） | 是 |
| `/markdown/:id` | Markdown 编辑器（编辑） | 是 |
| `/json` | JSON 格式化器（新建） | 是 |
| `/json/:id` | JSON 格式化器（编辑） | 是 |

路由守卫逻辑：
- 访问需认证页面但无 Token → 跳转登录页（携带 `redirect` 参数）
- 已登录访问登录/注册页 → 跳转首页或 `redirect` 目标
