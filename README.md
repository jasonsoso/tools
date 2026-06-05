# 工具包 (Tools)

在线工具集合，包含 Markdown 在线编辑器和 JSON 在线格式化器。

## 功能

- 📝 **Markdown 在线编辑器** — CodeMirror 6 编辑 + markdown-it 实时预览，支持 KaTeX 数学公式、表格插入、目录大纲、文档导出（.md / HTML）
- 🔧 **JSON 格式化** — 格式化 / 压缩 / 校验 JSON，树形视图，记录管理
- 🔐 **用户认证** — JWT 登录 / 注册，Spring Security 权限控制

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3, TypeScript, Vite, Tailwind CSS, Pinia, CodeMirror 6, markdown-it, KaTeX |
| 后端 | Spring Boot 3, JDK 17, Spring Security, JWT, MyBatis-Plus |
| 数据库 | MySQL 8 (生产) / H2 (开发/测试) |
| 测试 | Vitest (前端), JUnit 5 + Mockito (后端) |

## 快速开始

### 环境要求

- Node.js >= 18
- JDK 17
- Maven 3.6+
- MySQL 8 (可选，dev 模式使用 H2)

### 启动后端

```bash
cd tools-server

# 开发模式（H2 内存数据库，端口 8081）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产模式（MySQL，端口 8081）
mvn spring-boot:run
```

### 启动前端

```bash
cd tools-web
npm install
# 进行启动前端
npm run dev
```

访问 http://localhost:5173

### 运行测试

```bash
# 后端测试
cd tools-server && mvn test

# 前端测试
cd tools-web && npx vitest run
```

## 项目结构

```
tools/
├── tools-server/          # Spring Boot 后端
│   └── src/main/java/com/tools/
│       ├── config/        # Spring Security, CORS, 全局异常处理
│       ├── security/      # JWT 认证过滤器
│       ├── controller/    # REST API 控制器
│       ├── service/       # 业务逻辑层
│       ├── repository/    # 数据访问封装层
│       ├── mapper/        # MyBatis-Plus Mapper 接口
│       ├── entity/        # 数据库实体
│       ├── dto/           # 请求/响应 DTO
│       └── common/        # 统一响应包装
└── tools-web/             # Vue 3 前端
    └── src/
        ├── views/         # 页面组件
        ├── components/    # 通用组件
        ├── stores/        # Pinia 状态管理
        ├── api/           # API 请求封装
        ├── utils/         # 工具函数
        └── router/        # Vue Router 路由
```

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/markdown` | 获取文档列表 |
| GET | `/api/markdown/{id}` | 获取单个文档 |
| POST | `/api/markdown` | 创建文档 |
| PUT | `/api/markdown/{id}` | 更新文档 |
| DELETE | `/api/markdown/{id}` | 删除文档 |
| GET | `/api/json` | 获取 JSON 记录列表 |
| GET | `/api/json/{id}` | 获取单个记录 |
| POST | `/api/json` | 创建记录 |
| PUT | `/api/json/{id}` | 更新记录 |
| DELETE | `/api/json/{id}` | 删除记录 |
| GET | `/api/logs` | 获取操作日志（分页） |
