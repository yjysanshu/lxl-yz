# 部署指南

## 系统要求

### 软件环境
- JDK 17 或更高版本
- Node.js 16 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.8 或更高版本

### 硬件要求
- 内存：至少 2GB RAM
- 磁盘：至少 1GB 可用空间

## 部署步骤

### 1. 数据库配置

#### 1.1 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本（自动创建数据库和表）
source backend/src/main/resources/schema.sql

# 或者直接执行
mysql -u root -p < backend/src/main/resources/schema.sql
```

#### 1.2 验证数据库创建

```sql
USE distributor_db;
SHOW TABLES;

-- 应该看到以下表：
-- t_config
-- t_daily_statistics
-- t_distributor
-- t_order
-- t_team
```

### 2. 后端部署

#### 2.1 配置数据库连接

编辑 `backend/src/main/resources/application.properties`：

```properties
# 修改为你的数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/distributor_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

#### 2.2 构建项目

```bash
cd backend
mvn clean package
```

#### 2.3 启动服务

##### 开发环境
```bash
mvn spring-boot:run
```

##### 生产环境
```bash
java -jar target/yz-distributor-system-1.0.0.jar
```

#### 2.4 验证后端启动

访问：http://localhost:8080

查看日志确认启动成功：
```
Started DistributorSystemApplication in X.XXX seconds
```

### 3. 前端部署

#### 3.1 安装依赖

```bash
cd frontend
npm install
```

如果遇到网络问题，可以使用国内镜像：
```bash
npm install --registry=https://registry.npmmirror.com
```

#### 3.2 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:5173

#### 3.3 生产环境构建

```bash
npm run build
```

构建完成后，`dist` 目录包含所有静态文件，可以部署到 Nginx 或其他 Web 服务器。

### 4. Nginx 配置（可选）

#### 4.1 前端部署

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 代理后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### 4.2 重启 Nginx

```bash
sudo nginx -t
sudo nginx -s reload
```

## 功能验证

### 1. 验证数据抓取
1. 访问前端页面
2. 进入"数据抓取"页面
3. 点击"手动抓取数据"按钮
4. 检查是否有成功提示

### 2. 验证报表功能
1. 进入"每日分销报表"
2. 选择日期（如 2024-01-01）
3. 点击"查询"按钮
4. 应该能看到示例数据

### 3. 验证配置管理
1. 进入"系统配置"页面
2. 查看店铺Token配置
3. 修改并保存
4. 刷新页面验证是否保存成功

## 常见问题

### 1. 后端启动失败

**问题：** 数据库连接失败
```
Cannot create PoolableConnectionFactory
```

**解决方案：**
- 检查 MySQL 是否启动
- 确认数据库用户名和密码是否正确
- 确认数据库 `distributor_db` 是否已创建

### 2. 前端无法访问后端API

**问题：** CORS 错误或 404

**解决方案：**
- 确认后端服务已启动（http://localhost:8080）
- 检查 `frontend/vite.config.js` 中的代理配置
- 确认后端 CORS 配置正确

### 3. 前端安装依赖失败

**问题：** npm install 报错

**解决方案：**
```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules 和 package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

### 4. Maven 构建失败

**问题：** 依赖下载失败

**解决方案：**
配置国内Maven镜像，编辑 `~/.m2/settings.xml`：

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

## 日志查看

### 后端日志
日志位置：控制台输出

也可以配置日志文件，在 `application.properties` 中添加：
```properties
logging.file.name=logs/application.log
```

### 前端日志
打开浏览器开发者工具（F12）查看控制台日志

## 系统维护

### 数据库备份

```bash
# 备份数据库
mysqldump -u root -p distributor_db > backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p distributor_db < backup_20240101.sql
```

### 定时任务监控

系统包含两个定时任务：
1. 每小时抓取订单数据
2. 每天凌晨1点计算统计数据

查看日志确认定时任务是否正常执行：
```
定时任务：开始每小时订单数据抓取
定时任务：每小时订单数据抓取完成
```

## 性能优化建议

1. **数据库索引**：确保所有索引已正确创建（schema.sql 已包含）
2. **数据库连接池**：生产环境可增加连接池大小
3. **缓存**：可考虑添加 Redis 缓存热点数据
4. **日志级别**：生产环境将日志级别设为 WARN 或 ERROR

## 安全建议

1. 修改默认的数据库密码
2. 配置防火墙，只开放必要的端口
3. 使用 HTTPS（配置 SSL 证书）
4. 定期备份数据库
5. 保护好店铺 Token，不要泄露

## 扩展开发

### 添加新的报表
1. 在 `ReportService` 中添加查询方法
2. 在 `ReportController` 中添加 API 接口
3. 在前端创建新的报表页面
4. 在路由中注册新页面

### 修改第三方API对接
编辑 `DataFetchService.fetchFromThirdPartyAPI()` 方法，根据实际的第三方 API 进行调整。

## 技术支持

如有问题，请联系开发团队或提交 Issue。
