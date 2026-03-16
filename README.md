# RuoYi 后端模版项目

基于 RuoYi 框架的 Spring Boot 后端模版，提供用户、角色、菜单、部门等系统管理功能，可作为新项目的快速启动基础。

## 技术栈

- Java 8 / Spring Boot 2.5.15
- MyBatis-Plus
- Spring Security + JWT
- Redis
- MySQL 5.7+
- Druid 连接池
- Knife4j (Swagger 增强)

## 项目结构

```
├── ruoyi-admin      # Web 入口模块（Controller、启动类）
├── ruoyi-common     # 公共工具、实体基类、异常
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

### 1. 克隆项目

```bash
git clone <repository-url>
cd RuoYi-Template-Frontend
```

### 2. 初始化数据库

- 创建数据库（如 `ry`）
- 执行 `sql/ry_20250522.sql` 初始化表结构与基础数据

```bash
mysql -u root -p < sql/ry_20250522.sql
```

### 3. 修改配置

根据实际环境修改 `ruoyi-admin/src/main/resources/application-dev.yml` 中的配置（详见下方「需修改的配置」）。

### 4. 启动项目

```bash
mvn clean install
mvn spring-boot:run -pl ruoyi-admin
```

或在 IDE 中运行 `ruoyi-admin` 模块下的启动类。

### 5. 访问地址

- 接口地址：`http://localhost:8080/api`
- Swagger 文档：`http://localhost:8080/api/doc.html`
- Druid 监控：`http://localhost:8080/api/druid`（默认账号：ruoyi / 123456）

### 6. 默认账号

| 账号 | 密码 |
|------|------|
| admin | admin123 |

---

## 需修改的配置

### 1. 数据库配置

**位置**：`application-dev.yml`、`application-test.yml`、`application-prod.yml`

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root      # 改为实际数据库用户名
        password: password  # 改为实际数据库密码
```

- `url`：数据库地址、端口、库名、时区等，按实际环境修改
- `username` / `password`：数据库账号密码

### 2. Redis 配置

**位置**：各环境 `application-*.yml`

```yaml
spring:
  redis:
    host: localhost   # Redis 地址
    port: 6379        # Redis 端口
    database: 0       # 数据库索引（0-15）
    password:         # 有密码时填写
```

### 3. 文件上传路径（ruoyi.profile）

**位置**：各环境 `application-*.yml`

```yaml
ruoyi:
  profile: D:/ruoyi/uploadPath   # Windows 示例
  # profile: /home/ruoyi/uploadPath  # Linux 示例
```

- 用于头像、导入/导出文件、上传文件的存储目录
- 需确保应用有读写权限

### 4. 系统域名（ruoyi.domain）

**位置**：各环境 `application-*.yml`

```yaml
ruoyi:
  domain: http://localhost:8080   # 开发环境
  # domain: https://your-domain.com/api  # 生产环境
```

- 用于生成头像等资源的完整 URL

### 5. 应用端口与访问路径

**位置**：`application.yml`

```yaml
server:
  port: 8080              # 服务端口
  servlet:
    context-path: /api    # 应用访问路径前缀
```

### 6. Token 配置

**位置**：各环境 `application-*.yml`

```yaml
token:
  secret: abcdefghijklmnopqrstuvwxyz   # 生产环境务必更换
  expireTime: 30   # 令牌有效期（分钟）
```

- `secret`：JWT 密钥，生产环境必须使用强随机字符串

### 7. 环境切换

**位置**：`application.yml`

```yaml
spring:
  profiles:
    active: dev   # dev | test | prod
```

### 8. 日志级别

**位置**：各环境 `application-*.yml`

```yaml
logging:
  level:
    com.balsam.system: debug   # 可改为 info 降低日志量
```

### 9. RocketMQ（可选）

若使用 RocketMQ，在各环境 `application-*.yml` 中配置：

```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: dev
    send-message-timeout: 3000
```

不使用时可注释该配置块。

---

## 代码生成器

使用 MyBatis-Plus 代码生成器前，需在 `CodeGenerator` 中配置数据库连接和要生成的表。

**位置**：`ruoyi-admin/src/main/java/com/balsam/system/CodeGenerator.java`

```java
// 修改为实际数据库连接
String url = "jdbc:mysql://localhost:3306/ry?...";
String username = "root";
String password = "password";

// 设置要生成的表名
.addInclude("表名1", "表名2")
```

运行 `CodeGenerator.main()` 后，会在对应包下生成实体、Mapper、Service 等代码。

---

## 包名说明

当前包名为 `com.balsam.system`。新建业务时，可在其下增加子包，例如：

- `com.balsam.system.system`：系统管理
- `com.balsam.system.monitor`：监控
- 业务模块按需新建，如 `com.balsam.system.xxx`

修改顶层包名需要全局搜索替换，并同步更新 `application-*.yml` 中的 `mybatis-plus.typeAliasesPackage`。

---

## 常见问题

### 1. 启动失败：Redis 连接失败

确认 Redis 已启动，且 `application-dev.yml` 中的 host、port、password 配置正确。

### 2. 启动失败：数据库连接失败

确认 MySQL 已启动，数据库已创建，`url`、`username`、`password` 配置正确。

### 3. 上传文件或头像失败

检查 `ruoyi.profile` 配置的目录是否存在，且应用有读写权限。

### 4. Swagger 文档打不开

开发环境默认开启 Swagger，检查 `swagger.enabled: true`。生产环境建议关闭。

---

## 许可证

详见 [LICENSE](LICENSE) 文件。
