# RuoYi 后端模板项目

基于 RuoYi 的 Spring Boot 后端模板，内置用户、角色、菜单、部门等系统管理能力，可直接作为业务项目的后端基线。

## 技术栈

- Java 8 / Spring Boot 2.5.15
- MyBatis-Plus
- Spring Security + JWT
- Redis
- MySQL 5.7+
- Druid
- Knife4j（Swagger 增强）

## 项目结构

```text
├── ruoyi-admin      # Web 入口（Controller、启动类）
├── ruoyi-common     # 公共工具、基类、异常
├── ruoyi-framework  # 安全、Token、Redis、MyBatis 配置
├── ruoyi-system     # 系统管理（用户、角色、菜单、部门等）
└── sql/             # 数据库脚本
```

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis

## 快速开始

### 1) 拉取代码

```bash
git clone git@github.com:balsampears/backend-java-template.git
cd backend-java-template
```

### 2) 初始化数据库

1. 创建数据库（如 `ry`）
2. 执行脚本 `sql/ry_20250522.sql`

```bash
mysql -u root -p < sql/ry_20250522.sql
```

### 3) 修改开发环境配置

编辑 `ruoyi-admin/src/main/resources/application-dev.yml`，至少完成下列配置：

- 数据库：`spring.datasource.druid.master`
- Redis：`spring.redis`
- 上传目录：`ruoyi.profile`
- 域名：`ruoyi.domain`
- Token 密钥：`token.secret`

### 4) 启动项目

```bash
mvn clean install
mvn spring-boot:run -pl ruoyi-admin
```

也可以在 IDE 中运行 `ruoyi-admin` 模块启动类。

### 5) 访问地址

- API：`http://localhost:8080/api`
- Swagger：`http://localhost:8080/api/doc.html`
- Druid：`http://localhost:8080/api/druid`（默认 `ruoyi / 123456`）

### 6) 默认账号

| 账号  | 密码     |
| ----- | -------- |
| admin | admin123 |

---

## 配置说明（按优先级）

> 配置文件目录：`ruoyi-admin/src/main/resources/`

### 1) 数据库配置（必改）

**文件**：`application-dev.yml`、`application-test.yml`、`application-prod.yml`

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: password
```

### 2) Redis 配置（必改）

**文件**：各环境 `application-*.yml`

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    password:
```

### 3) 上传目录 `ruoyi.profile`（必改）

**文件**：各环境 `application-*.yml`

```yaml
ruoyi:
  profile: D:/ruoyi/uploadPath
  # profile: /home/ruoyi/uploadPath
```

确保目录存在且应用具备读写权限。

### 4) 域名 `ruoyi.domain`（建议改）

**文件**：各环境 `application-*.yml`

```yaml
ruoyi:
  domain: http://localhost:8080
  # domain: https://your-domain.com/api
```

用于拼接头像等资源访问 URL。

### 5) 端口与上下文路径

**文件**：`application.yml`

```yaml
server:
  port: 8080
  servlet:
    context-path: /api
```

### 6) Token 配置（生产必改）

**文件**：各环境 `application-*.yml`

```yaml
token:
  secret: abcdefghijklmnopqrstuvwxyz
  expireTime: 30
```

`token.secret` 在生产环境必须替换为强随机字符串。

### 7) 环境切换

**文件**：`application.yml`

```yaml
spring:
  profiles:
    active: dev
```

可选值：`dev | test | prod`

### 8) 日志级别

**文件**：各环境 `application-*.yml`

```yaml
logging:
  level:
    com.balsam.system: debug
```

生产建议调整为 `info` 或更高。

### 9) RocketMQ（可选）

**文件**：各环境 `application-*.yml`

```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: dev
    send-message-timeout: 3000
```

未使用可注释或删除该配置块。

---

## 生产环境上线前检查

- [ ] 修改 `token.secret`，避免默认值
- [ ] 关闭 Swagger（`swagger.enabled: false`）
- [ ] 收紧 Druid 监控访问策略（账号、密码、白名单）
- [ ] 使用独立的数据库和 Redis 账号，最小权限
- [ ] 确认 `ruoyi.profile` 目录权限与磁盘容量
- [ ] 根据流量调整日志级别与日志保留策略

---

## 代码生成器

使用 MyBatis-Plus 代码生成器前，先修改：

**文件**：`ruoyi-admin/src/main/java/com/balsam/system/CodeGenerator.java`

```java
// 数据库连接
String url = "jdbc:mysql://localhost:3306/ry?...";
String username = "root";
String password = "password";

// 要生成的表
.addInclude("table_a", "table_b")
```

执行 `CodeGenerator.main()` 后生成实体、Mapper、Service 等代码。

---

## 包名说明

当前顶层包名：`com.balsam.system`

可按业务扩展子包，例如：

- `com.balsam.system.system`
- `com.balsam.system.monitor`
- `com.balsam.system.xxx`

若修改顶层包名，需全局替换并同步更新 `application-*.yml` 中 `mybatis-plus.typeAliasesPackage`。

---

## 常见问题

### 启动失败：Redis 连接失败

确认 Redis 已启动，且 `spring.redis.host/port/password` 正确。

### 启动失败：数据库连接失败

确认 MySQL 已启动、数据库已创建，且 `url/username/password` 正确。

### 文件上传或头像上传失败

确认 `ruoyi.profile` 目录存在且有读写权限。

### Swagger 文档打不开

检查是否开启 Swagger（开发环境通常为 `swagger.enabled: true`）。

---

## 许可证

详见 [LICENSE](LICENSE)。
