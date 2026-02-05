# 快速开始指南

本指南将帮助您在 10 分钟内启动并运行分销员订单数据管理系统。

## 前提条件

确保您的系统已安装：
- ✅ JDK 17 或更高版本
- ✅ Node.js 16 或更高版本  
- ✅ MySQL 8.0 或更高版本
- ✅ Maven 3.8 或更高版本

## 第一步：准备数据库

### 1.1 创建数据库并导入数据

```bash
# 进入项目目录
cd /path/to/lxl-yz

# 执行数据库初始化脚本
mysql -u root -p < backend/src/main/resources/schema.sql

# 或者登录MySQL后执行
mysql -u root -p
source backend/src/main/resources/schema.sql
```

### 1.2 验证数据库

```sql
USE distributor_db;
SHOW TABLES;
```

应该看到 5 张表：
- t_config
- t_daily_statistics  
- t_distributor
- t_order
- t_team

## 第二步：配置后端

### 2.1 修改数据库连接

编辑文件：`backend/src/main/resources/application.properties`

```properties
# 修改为您的数据库配置
spring.datasource.username=root
spring.datasource.password=your_password
```

### 2.2 启动后端服务

```bash
cd backend

# 方式1：使用 Maven 直接运行（推荐用于开发）
mvn spring-boot:run

# 方式2：打包后运行（推荐用于生产）
mvn clean package
java -jar target/yz-distributor-system-1.0.0.jar
```

### 2.3 验证后端启动

打开浏览器访问：http://localhost:8080

看到类似以下日志表示成功：
```
Started DistributorSystemApplication in X.XXX seconds
```

## 第三步：启动前端

### 3.1 安装依赖

```bash
cd frontend
npm install

# 如果遇到网络问题，可使用国内镜像
npm install --registry=https://registry.npmmirror.com
```

### 3.2 启动开发服务器

```bash
npm run dev
```

### 3.3 访问系统

打开浏览器访问：http://localhost:5173

您应该能看到系统的登录界面。

## 第四步：开始使用

### 4.1 查看示例数据

系统已自动导入示例数据：
- 3 个团队
- 5 个分销员
- 5 条订单（使用当天日期）

### 4.2 查看每日报表

1. 点击左侧菜单 "每日分销报表"
2. 选择今天的日期
3. 点击 "查询" 按钮
4. 应该能看到示例订单数据

### 4.3 配置店铺Token

1. 点击左侧菜单 "系统配置"
2. 输入您的店铺 Token
3. 点击 "保存配置"

### 4.4 手动抓取数据

1. 点击左侧菜单 "数据抓取"
2. 点击 "手动抓取数据" 按钮
3. 查看执行结果

**注意：** 目前第三方API是模拟的，需要在实际使用时对接真实的API。

## 常见启动问题

### 问题 1：后端启动失败 - 数据库连接错误

**错误信息：**
```
Cannot create PoolableConnectionFactory
```

**解决方案：**
1. 确认 MySQL 服务已启动
2. 检查用户名和密码是否正确
3. 确认数据库 `distributor_db` 已创建

### 问题 2：前端启动失败 - 端口被占用

**错误信息：**
```
Port 5173 is already in use
```

**解决方案：**
```bash
# 方式1：结束占用端口的进程
lsof -ti:5173 | xargs kill -9

# 方式2：修改端口（编辑 vite.config.js）
server: {
  port: 5174  // 改为其他端口
}
```

### 问题 3：npm install 失败

**解决方案：**
```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules
rm -rf node_modules package-lock.json

# 使用国内镜像重新安装
npm install --registry=https://registry.npmmirror.com
```

### 问题 4：Maven 下载依赖慢

**解决方案：**

配置阿里云 Maven 镜像，编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

## 系统菜单说明

| 菜单项 | 功能说明 |
|--------|----------|
| 数据抓取 | 手动触发订单数据抓取 |
| 每日分销报表 | 查看每日分销员业绩，支持按日期和团队筛选 |
| 月度汇总报表 | 查看月度分销员业绩汇总 |
| 周度汇总报表 | 查看周度分销员业绩汇总 |
| 系统配置 | 管理店铺Token等系统配置 |

## 下一步

系统已成功启动！接下来您可以：

1. **对接真实的第三方API**
   - 修改 `DataFetchService.fetchFromThirdPartyAPI()` 方法
   - 实现真实的数据抓取逻辑

2. **查看完整文档**
   - `README.md` - 项目说明
   - `DEPLOYMENT.md` - 部署指南
   - `DEVELOPMENT.md` - 开发指南
   - `DELIVERY.md` - 交付文档

3. **开始自定义开发**
   - 添加新的报表
   - 扩展业务功能
   - 调整界面样式

## 需要帮助？

- 📖 查看完整文档
- 🐛 提交 Issue
- 💬 联系技术支持

---

**恭喜！您已经成功启动了分销员订单数据管理系统！** 🎉
