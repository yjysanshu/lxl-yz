# 🚀 配置快速参考

## 📋 完成清单

- ✅ 定时任务配置化（从 `application.yml` 读取 cron 表达式）
- ✅ 创建 YML 格式配置文件
- ✅ 开发环境配置（application-dev.yml）
- ✅ 生产环境配置（application-prod.yml）
- ✅ 删除旧的 properties 文件
- ✅ 更新 ScheduledTasks.java
- ✅ 创建配置文档

## 🎯 核心改动

### ScheduledTasks.java
```java
// 之前：硬编码
@Scheduled(cron = "0 * * * * ?")

// 现在：配置化
@Scheduled(cron = "${scheduled.fetch-order-data.cron}")
```

### 配置文件格式
```
之前: application.properties ❌
现在: application.yml ✅
```

## ⚡ 快速切换环境

### 开发环境（默认）
```yaml
# application.yml
spring:
  profiles:
    active: dev
```

### 生产环境
```yaml
# application.yml
spring:
  profiles:
    active: prod
```

或命令行：
```bash
java -jar app.jar --spring.profiles.active=prod
```

## 📊 环境差异速览

| 项目 | 开发环境 | 生产环境 |
|-----|---------|---------|
| **订单抓取** | 每10分钟 | 每小时 |
| **统计计算** | 凌晨2点 | 凌晨1点 |
| **日志** | DEBUG | INFO/WARN |
| **数据库** | localhost | prod-db-host |
| **DDL** | update | validate |

## 📁 文件位置

```
backend/src/main/resources/
├── application.yml           ← 主配置
├── application-dev.yml       ← 开发环境
└── application-prod.yml      ← 生产环境

backend/src/main/java/com/lxl/yz/scheduled/
└── ScheduledTasks.java       ← 定时任务

项目根目录/
├── CONFIG_MIGRATION_SUMMARY.md   ← 迁移总结
├── YML_CONFIG_GUIDE.md           ← YML 指南
└── SCHEDULED_TASKS_CONFIG.md     ← 任务配置
```

## 🔧 常用修改

### 修改定时任务时间
编辑对应环境的 YML 文件：
```yaml
scheduled:
  fetch-order-data:
    cron: "0 0/30 * * * ?"  # 每30分钟
```

### Cron 表达式常用示例
```
0 * * * * ?       # 每分钟
0 */5 * * * ?     # 每5分钟
0 0 * * * ?       # 每小时
0 0 1 * * ?       # 每天凌晨1点
0 0 0 * * MON     # 每周一凌晨
```

## ⚠️ 生产部署检查

部署前务必修改：
1. 数据库地址
2. 数据库用户名/密码
3. 定时任务时间
4. 环境切换到 prod

## 📚 详细文档

- `CONFIG_MIGRATION_SUMMARY.md` - 完整迁移总结
- `YML_CONFIG_GUIDE.md` - YML 格式详细指南
- `SCHEDULED_TASKS_CONFIG.md` - 定时任务完整说明

---
最后更新：2026-02-05
