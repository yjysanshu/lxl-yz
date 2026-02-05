# lxl-yz 分销员订单数据管理系统

## 项目简介
从第三方店铺后台抓取分销员订单数据，进行统计分析和展示，支持多维度查询和自动化数据同步。

## 技术栈
- 后端：Java Spring Boot 3.2.0 + Spring Data JPA + MySQL
- 前端：Vue 3 + Element Plus + Vite
- 数据库：MySQL 8.0+

## 功能模块

### 1. 数据抓取模块
- **自动抓取**：定时任务每隔 1 小时自动执行
- **手动抓取**：管理员在后台点击按钮立即触发
- **去重策略**：根据订单 ID 判断，如已存在则更新，不存在则新增

### 2. 每日分销报表
- 展示分销员名称、已支付订单数、订单总金额、客单价
- 支持按日期和团队筛选

### 3. 月度汇总报表
- 展示分销员名称、成交人数、已支付订单数、订单总金额、客单价
- 支持按月份和团队筛选

### 4. 周度汇总报表
- 展示分销员名称、成交人数、已支付订单数、订单总金额、客单价
- 支持按周和团队筛选

### 5. 系统配置管理
- 店铺 Token 配置
- 支持查看和修改

## 项目结构

```
lxl-yz/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/lxl/yz/
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── repository/       # 数据访问层
│   │   │   │   ├── service/          # 业务逻辑层
│   │   │   │   ├── controller/       # 控制器
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── config/           # 配置类
│   │   │   │   └── scheduled/        # 定时任务
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── schema.sql        # 数据库初始化脚本
│   │   └── test/
│   └── pom.xml
└── frontend/                   # 前端项目
    ├── src/
    │   ├── api/                # API接口
    │   ├── components/         # 组件
    │   ├── views/              # 页面
    │   ├── router/             # 路由
    │   ├── utils/              # 工具类
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── vite.config.js
    └── package.json
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.8+

### 数据库配置

1. 创建数据库并执行初始化脚本：

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2. 修改后端配置文件 `backend/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/distributor_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端应用将在 http://localhost:5173 启动

## API 接口说明

### 数据管理
- `POST /api/data/fetch` - 手动抓取数据

### 报表查询
- `GET /api/report/daily` - 查询每日报表
- `GET /api/report/monthly` - 查询月度报表
- `GET /api/report/weekly` - 查询周度报表

### 团队管理
- `GET /api/team/list` - 获取团队列表

### 系统配置
- `GET /api/config/{key}` - 获取配置
- `PUT /api/config/{key}` - 更新配置

## 数据库设计

### 主要表结构

1. **t_team** - 团队表
2. **t_distributor** - 分销员表
3. **t_order** - 订单表
4. **t_daily_statistics** - 每日统计表
5. **t_config** - 系统配置表

详细的表结构请参考 `backend/src/main/resources/schema.sql`

## 定时任务

- 每小时自动抓取订单数据：`0 0 * * * ?`
- 每天凌晨1点计算每日统计：`0 0 1 * * ?`

## 注意事项

1. 第三方 API 调用需要在 `DataFetchService` 中配置真实的 API 地址和参数
2. 统计仅计算 `order_status = "已支付"` 的订单
3. 客单价 = 订单总金额 ÷ 订单数量
4. 成交人数按 `customer_id` 去重统计

## 许可证
MIT License