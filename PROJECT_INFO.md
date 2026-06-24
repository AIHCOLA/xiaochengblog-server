# Xiao Cheng Blog Server

> 小橙博客后端服务 — 基于 Spring Boot 3.2 的全栈个人博客 API

## 技术栈

| 层级 | 技术 |
|------|------|
| 框架 | Spring Boot 3.2.5 |
| 语言 | Java 17 |
| ORM | MyBatis-Plus 3.5.10 |
| 数据库 | MySQL 8.x |
| 安全 | Spring Security + JWT + OAuth2 |
| 缓存 | Caffeine (本地缓存) |
| 限流 | Bucket4j |
| API 文档 | SpringDoc OpenAPI (Swagger UI) |
| 音乐 API | Node.js + NeteaseCloudMusicApi |

## 功能模块

### 博客核心
- **文章管理** — 文章的 CRUD，支持 Markdown 内容、分类、多标签
- **分类与标签** — 文章分类和标签体系
- **评论系统** — 支持嵌套回复
- **收藏** — 用户收藏文章
- **阅读历史** — 记录用户阅读过的文章

### 用户系统
- **邮箱注册/登录** — JWT 认证，access_token (24h) + refresh_token (7d)
- **OAuth2 三方登录** — 支持 GitHub、Google、微信扫码登录
- **用户资料** — 头像、简介、社交链接

### 工具箱
- **心情记录** — 每日 emoji 心情打卡
- **待办事项** — 个人 Todo 列表，支持拖拽排序
- **倒计时** — 自定义倒计时事件
- **闪卡** — 已知单词/知识点标记
- **健康提醒** — 自定义健康习惯提醒（喝水、久坐等）
- **快捷链接** — 个人书签/快捷方式
- **便签** — 个人便签（每个用户一张）

### 其他
- **留言板** — 公开留言
- **AI 聊天记录** — 保存 AI 对话历史
- **音乐播放列表** — 对接网易云音乐 API，管理个人播放列表
- **天气** — 基于 IP 定位获取当前天气

## 项目结构

```
xiaochengblog-server/
├── pom.xml                          # Maven 配置
├── src/main/java/com/xiaochengblog/
│   ├── BlogApplication.java         # 启动入口
│   ├── config/                      # 配置类 (CORS, Cache, MyBatis-Plus, 数据初始化)
│   ├── controller/                  # REST 控制器
│   ├── service/                     # 业务逻辑层
│   ├── mapper/                      # MyBatis Mapper 接口
│   ├── model/                       # 实体类
│   ├── dto/                         # 数据传输对象
│   ├── security/                    # 安全相关 (JWT, OAuth2, 限流)
│   └── exception/                   # 全局异常处理
├── src/main/resources/
│   ├── application.yml              # 应用配置
│   └── schema.sql                   # 数据库建表脚本
└── music-api/                       # 音乐 API 子服务 (Node.js)
    ├── package.json
    └── app.js
```

## 启动方式

### 前置条件

- JDK 17+
- Maven 3.6+
- MySQL 8.x
- Node.js 18+ (仅音乐 API 需要)

### 1. 数据库准备

创建 MySQL 数据库：

```sql
CREATE DATABASE xiaocheng DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行建表脚本：

```bash
mysql -u root -p -P 3308 xiaocheng < src/main/resources/schema.sql
```

### 2. 配置环境变量（可选）

OAuth2 三方登录需要配置对应的 Client ID / Secret：

| 变量 | 说明 |
|------|------|
| `GITHUB_CLIENT_ID` | GitHub OAuth App Client ID |
| `GITHUB_CLIENT_SECRET` | GitHub OAuth App Client Secret |
| `GOOGLE_CLIENT_ID` | Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth Client Secret |
| `WECHAT_APP_ID` | 微信开放平台 App ID |
| `WECHAT_APP_SECRET` | 微信开放平台 App Secret |
| `JWT_SECRET` | JWT 签名密钥 |

不配置也可以启动，会使用默认值，但 OAuth2 登录会不可用。

### 3. 启动后端服务

```bash
# 在项目根目录执行
mvn spring-boot:run
```

或者用 IDE 直接运行 `BlogApplication.java` 的 main 方法。

服务启动后：
- API 地址: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs (JSON): `http://localhost:8080/api-docs`

### 4. 启动音乐 API (可选)

```bash
cd music-api
npm install
npm start
```

音乐 API 运行在 `http://localhost:3000`。

### 5. 默认数据库

首次启动时，`DataInitializer` 会自动插入演示数据（仅当 users 表为空时），包括：

- 6 个文章分类
- 16 个标签
- 6 篇示例文章
- 1 个管理员账号

**演示账号**: `demo@xiaocheng.dev` / `demo123`

## 数据库连接

默认配置在 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xiaocheng
    username: root
    password: root
```

如果 MySQL 端口或密码不同，请修改 `application.yml` 中的对应配置。

## 前端对接

服务端 CORS 已配置允许以下前端地址跨域访问：

- `http://localhost:5173` (Vite 默认)
- `http://localhost:5174`
- `http://localhost:4173` (Vite preview)

API 路径前缀统一为 `/api/`。

## 关键技术点

- **认证流程**: 登录后 JWT 通过 httpOnly Cookie 下发，前端无需手动管理 Token
- **逻辑删除**: MyBatis-Plus 配置了逻辑删除字段 `deleted`，删除操作不会物理删除数据
- **缓存策略**: 使用 Caffeine 本地缓存，最大 500 条，10 分钟过期
- **限流保护**: 登录/注册接口有令牌桶限流（10 次/分钟）
- **API 文档**: 开发环境下访问 Swagger UI 可查看和测试所有接口
