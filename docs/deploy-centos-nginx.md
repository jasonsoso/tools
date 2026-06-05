# Tools 项目部署教程（CentOS + Nginx）

## 项目结构

```
tools-server/    Spring Boot 3.2 后端 (Java 17, Maven, MySQL)
tools-web/       Vue 3 前端 (Vite, TypeScript)
```

**核心架构：** Nginx 反代 → 前端静态文件 + `/api` 代理到后端 `:8081`

```
用户 → Nginx (:80)
         ├─ /               → 前端静态文件 (dist/)
         └─ /api/*          → 后端 Spring Boot (:8081)
```

## 1. 环境准备（CentOS 服务器）

### 1.1 更新系统

```bash
ssh root@YOUR_SERVER_IP
yum update -y
```

### 1.2 安装 Java 17

```bash
# CentOS 7/8
yum install -y java-17-openjdk java-17-openjdk-devel

# 或 CentOS Stream 9+
dnf install -y java-17-openjdk java-17-openjdk-devel

# 验证
java -version
# 预期: openjdk version "17.0.x"
```

### 1.3 安装 MySQL 8

```bash
# 添加 MySQL 官方仓库
rpm -Uvh https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

# 安装
yum install -y mysql-community-server

# 启动
systemctl start mysqld
systemctl enable mysqld

# 获取初始密码
grep 'temporary password' /var/log/mysqld.log
```

**创建数据库和用户：**

```bash
mysql -u root -p
```

```sql
-- 输入初始密码后修改 root 密码
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword!';

-- 创建数据库
CREATE DATABASE tools_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 可选：创建专用用户
CREATE USER 'tools'@'localhost' IDENTIFIED BY 'ToolsPass123!';
GRANT ALL PRIVILEGES ON tools_db.* TO 'tools'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**初始化表结构：**

```bash
mysql -u root -p tools_db < /opt/tools/tools-server/src/main/resources/db/schema.sql
```

### 1.4 安装 Nginx

```bash
yum install -y nginx

# 启动
systemctl start nginx
systemctl enable nginx

# 验证（浏览器访问 http://YOUR_SERVER_IP，应看到 Nginx 欢迎页）
curl http://localhost
```

## 2. 构建项目

### 2.1 在本机构建后端

```bash
# 进入后端目录
cd D:/ai/tools/tools-server

# 先修改生产配置，确保 MySQL 连接信息正确
# 编辑 src/main/resources/application.yml
#   spring.datasource.url: jdbc:mysql://localhost:3306/tools_db
#   spring.datasource.username: root (或你创建的 tools 用户)
#   spring.datasource.password: YourStrongPassword!

# 打包（跳过测试，方便快速构建）
mvn clean package -DskipTests

# 产物: target/tools-server-0.0.1-SNAPSHOT.jar
```

### 2.2 在本机构建前端

```bash
cd D:/ai/tools/tools-web

npm install
npm run build

# 产物: dist/ 目录
```

## 3. 上传到服务器

```bash
# 在服务器上创建目录结构
ssh root@YOUR_SERVER_IP
mkdir -p /opt/tools/backend /opt/tools/frontend

# 退出服务器，在本机执行上传
exit

# 上传后端 jar（PowerShell / Git Bash）
scp tools-server/target/tools-server-0.0.1-SNAPSHOT.jar root@YOUR_SERVER_IP:/opt/tools/backend/

# 上传前端 dist
scp -r tools-web/dist/* root@YOUR_SERVER_IP:/opt/tools/frontend/
```

## 4. 服务器配置

### 4.1 修改后端配置（如数据库密码和服务器不同）

如果服务器上的 MySQL 密码和本地不同，用环境变量覆盖：

```bash
# 在服务器上
ssh root@YOUR_SERVER_IP
cat > /opt/tools/backend/application-prod.yml << 'EOF'
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tools_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: YourStrongPassword!
  jpa:
    hibernate:
      ddl-auto: none
server:
  port: 8081
EOF
```

### 4.2 配置 Nginx

```bash
cat > /etc/nginx/conf.d/tools.conf << 'EOF'
# 后端 API 代理 — upstream 定义
upstream tools_backend {
    server 127.0.0.1:8081;
    keepalive 64;
}

server {
    listen       80;
    server_name  YOUR_DOMAIN_OR_IP;   # 改成你的域名或 IP

    # 日志
    access_log  /var/log/nginx/tools.access.log;
    error_log   /var/log/nginx/tools.error.log;

    # 前端静态文件
    location / {
        root   /opt/tools/frontend;
        index  index.html;
        try_files $uri $uri/ /index.html;   # SPA 路由必需
    }

    # 静态资源缓存（JS/CSS/图片/字体）
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        root      /opt/tools/frontend;
        expires   30d;
        add_header Cache-Control "public, immutable";
    }

    # API 代理到 Spring Boot 后端
    location /api/ {
        proxy_pass http://tools_backend;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header Connection "";
        proxy_read_timeout 60s;
    }
}
EOF

# 测试配置
nginx -t

# 重载
systemctl reload nginx
```

### 4.3 配置后端 Systemd 服务

```bash
cat > /etc/systemd/system/tools-backend.service << 'EOF'
[Unit]
Description=Tools Backend Service
After=network.target mysqld.service
Wants=mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/tools/backend
ExecStart=/usr/bin/java -jar /opt/tools/backend/tools-server-0.0.1-SNAPSHOT.jar --spring.config.additional-location=/opt/tools/backend/application-prod.yml
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# 启动
systemctl daemon-reload
systemctl start tools-backend
systemctl enable tools-backend

# 查看状态
systemctl status tools-backend

# 查看日志
journalctl -u tools-backend -f
```

## 5. 防火墙配置

```bash
# 开放 80 端口
firewall-cmd --permanent --add-port=80/tcp

# 如果 SSH 端口不是 22，确保 22 开放
firewall-cmd --permanent --add-port=22/tcp

# 8081 不对外暴露（仅 Nginx 通过 localhost 访问）
# 不需要开放 8081

# 应用
firewall-cmd --reload

# 验证
firewall-cmd --list-all
```

## 6. 验证部署

```bash
# 1. 检查 Nginx
systemctl status nginx
curl -I http://localhost
# 预期: HTTP/1.1 200 OK

# 2. 检查后端
systemctl status tools-backend
curl http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"test"}'
# 预期: JSON 响应（即使是 401，说明后端正常运行）

# 3. 检查 API 代理
curl http://localhost/api/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"test"}'
# 预期: 同上的 JSON 响应

# 4. 浏览器访问 http://YOUR_SERVER_IP
# 应看到前端页面，注册/登录后可使用
```

## 7. 日常运维

```bash
# 查看后端日志
journalctl -u tools-backend -f

# 重启后端
systemctl restart tools-backend

# 更新前端（构建后上传覆盖）
scp -r dist/* root@YOUR_SERVER_IP:/opt/tools/frontend/
# 前端无需重启（静态文件），刷新浏览器即可

# 更新后端
scp tools-server-0.0.1-SNAPSHOT.jar root@YOUR_SERVER_IP:/opt/tools/backend/
systemctl restart tools-backend
```

## 8. HTTPS 配置（可选）

```bash
# 使用 Certbot 免费 SSL 证书
yum install -y certbot python3-certbot-nginx

# 自动配置（需域名已解析到服务器）
certbot --nginx -d YOUR_DOMAIN

# 证书自动续期
certbot renew --dry-run
```

## 目录总览

```
服务器部署目录:
/opt/tools/
├── backend/
│   ├── tools-server-0.0.1-SNAPSHOT.jar
│   └── application-prod.yml
└── frontend/
    ├── index.html
    ├── assets/
    │   ├── *.js
    │   ├── *.css
    │   └── *.woff2
    └── ...

配置文件:
/etc/nginx/conf.d/tools.conf              # Nginx 配置
/etc/systemd/system/tools-backend.service # 后端服务
```

## 故障排查

| 问题 | 检查 |
|------|------|
| 后端起不来 | `journalctl -u tools-backend -f` 看 Java 错误，检查 MySQL 连接 |
| 前端 404 | `nginx -t` 检查配置，确认 `try_files` 有 `/index.html` |
| API 502 | 后端是否启动？`systemctl status tools-backend`；Nginx upstream 端口是否正确 |
| 跨域错误 | 检查 nginx proxy_pass 是否正确转发 `/api/`；前端 baseURL 是否为 `/api` |
| MySQL 连不上 | `systemctl status mysqld`；检查 `application-prod.yml` 中账号密码 |
