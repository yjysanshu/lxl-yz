d# 定时任务配置说明

## 概述

定时任务的执行时间已经配置化，支持开发环境（dev）和生产环境（prod）不同的执行时间。

## 配置文件（YML 格式）

### 1. application.yml
主配置文件，设置默认的 profile 和基础配置。

```yaml
spring:
  profiles:
    active: dev
```

### 2. application-dev.yml
开发环境配置：
- **订单数据抓取**: 每10分钟执行一次 (`0 */10 * * * ?`)
- **每日统计计算**: 每天凌晨2点执行 (`0 0 2 * * ?`)
- 日志级别为 DEBUG，方便调试
- 使用本地数据库

### 3. application-prod.yml
生产环境配置：
- **订单数据抓取**: 每小时执行一次 (`0 0 * * * ?`)
- **每日统计计算**: 每天凌晨1点执行 (`0 0 1 * * ?`)
- 日志级别为 INFO/WARN，减少日志输出
- 需要配置生产数据库连接信息

## 环境切换

### 方式1: 修改 application.yml
```yaml
spring:
  profiles:
    active: dev  # 开发环境
    # active: prod # 生产环境
```

### 方式2: 命令行参数
```bash
# 开发环境
java -jar app.jar --spring.profiles.active=dev

# 生产环境
java -jar app.jar --spring.profiles.active=prod
```

### 方式3: 环境变量
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

## Cron 表达式说明

Cron 表达式格式：`秒 分 时 日 月 周`

示例：
- `0 * * * * ?` - 每分钟执行
- `0 */10 * * * ?` - 每10分钟执行
- `0 0 * * * ?` - 每小时执行
- `0 0 1 * * ?` - 每天凌晨1点执行
- `0 0 2 * * ?` - 每天凌晨2点执行
- `0 0 0 * * MON` - 每周一凌晨执行

## 定时任务列表

### 1. fetchOrderDataHourly
- **功能**: 自动抓取订单数据
- **配置项**: `scheduled.fetch-order-data.cron`
- **开发环境**: 每10分钟执行一次
- **生产环境**: 每小时执行一次

### 2. calculateDailyStatistics
- **功能**: 计算每日统计数据
- **配置项**: `scheduled.calculate-daily-statistics.cron`
- **开发环境**: 每天凌晨2点执行
- **生产环境**: 每天凌晨1点执行

## 注意事项

1. **生产环境数据库配置**: 在部署到生产环境前，必须修改 `application-prod.properties` 中的数据库连接信息
2. **时区**: 所有定时任务使用 `Asia/Shanghai` 时区
3. **安全性**: 生产环境配置文件中的密码应使用环境变量或加密配置管理
4. **调试**: 开发环境的定时任务频率较高，便于测试；生产环境频率较低，节省资源
