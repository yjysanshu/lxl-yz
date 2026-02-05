# YML 配置文件说明

## 📁 配置文件结构

```
backend/src/main/resources/
├── application.yml           # 主配置文件
├── application-dev.yml       # 开发环境配置
├── application-prod.yml      # 生产环境配置
└── schema.sql                # 数据库脚本
```

## 📝 配置文件内容

### application.yml（主配置）
```yaml
spring:
  application:
    name: yz-distributor-system
  profiles:
    active: dev

server:
  port: 8080

# Scheduled Tasks Configuration
scheduled:
  fetch-order-data:
    cron: "0 * * * * ?"
  calculate-daily-statistics:
    cron: "0 0 1 * * ?"
```

### application-dev.yml（开发环境）
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/distributor_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: S9zSUHa67p%yZzsEFQsm
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

logging:
  level:
    com.lxl.yz: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# Scheduled Tasks Configuration (Development)
scheduled:
  fetch-order-data:
    # Fetch order data every 10 minutes (for testing purposes)
    cron: "0 */10 * * * ?"
  calculate-daily-statistics:
    # Calculate daily statistics at 2:00 AM every day
    cron: "0 0 2 * * ?"
```

### application-prod.yml（生产环境）
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-host:3306/distributor_db?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: prod_user
    password: CHANGE_THIS_PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.MySQLDialect

logging:
  level:
    com.lxl.yz: INFO
    org.springframework.web: WARN
    org.hibernate.SQL: WARN

# Scheduled Tasks Configuration (Production)
scheduled:
  fetch-order-data:
    # Fetch order data every hour
    cron: "0 0 * * * ?"
  calculate-daily-statistics:
    # Calculate daily statistics at 1:00 AM every day
    cron: "0 0 1 * * ?"
```

## 🔧 YML vs Properties 对比

### Properties 格式
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db
spring.datasource.username=root
spring.datasource.password=password
scheduled.fetch-order-data.cron=0 * * * * ?
```

### YML 格式（更清晰的层级结构）
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db
    username: root
    password: password

scheduled:
  fetch-order-data:
    cron: "0 * * * * ?"
```

## ✨ YML 格式优势

1. **层级结构清晰** - 使用缩进表示层级关系，更易读
2. **避免重复** - 不需要重复写前缀（如 `spring.datasource.`）
3. **支持复杂数据** - 可以直接定义列表、对象等复杂结构
4. **注释更友好** - 使用 `#` 注释，与配置项更好地融合
5. **紧凑性** - 相同配置通常更短

## 🚀 使用方式

### 切换环境

**方式1：修改 application.yml**
```yaml
spring:
  profiles:
    active: prod  # 改为 dev 或 prod
```

**方式2：命令行参数**
```bash
java -jar app.jar --spring.profiles.active=prod
```

**方式3：环境变量**
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

### 自定义 Cron 表达式

只需修改对应环境的配置文件：

```yaml
scheduled:
  fetch-order-data:
    cron: "0 0/30 * * * ?"  # 改为每30分钟执行
  calculate-daily-statistics:
    cron: "0 0 3 * * ?"     # 改为凌晨3点执行
```

## ⚠️ 注意事项

1. **YML 对缩进敏感** - 必须使用空格（不能用 Tab），通常使用 2 个空格
2. **Cron 表达式需要引号** - 在 YML 中，cron 表达式要用引号包裹
3. **特殊字符转义** - 某些特殊字符可能需要转义或用引号包裹
4. **冒号后要有空格** - `key: value` 中冒号后必须有空格

## 🔒 生产环境部署前检查

在部署到生产环境前，请确保：

- [ ] 修改 `application-prod.yml` 中的数据库地址
- [ ] 修改 `application-prod.yml` 中的数据库用户名和密码
- [ ] 检查定时任务的 cron 表达式是否符合要求
- [ ] 确认 `spring.jpa.hibernate.ddl-auto` 设置为 `validate`（生产环境不应自动修改表结构）
- [ ] 设置 `spring.profiles.active=prod` 或使用环境变量
