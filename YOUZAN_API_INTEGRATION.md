# 有赞API集成文档

## 概述

本文档说明如何配置和使用有赞订单数据抓取功能。

## API接口信息

### 接口地址
```
https://www.youzan.com/v4/ump/new-salesman/order/getList.json
```

### 请求方法
GET

### 请求参数
- `pageSize`: 每页数量（默认20）
- `page`: 页码（从1开始）
- `timeType`: 时间类型（1=订单创建时间）
- `startTime`: 开始时间（Unix时间戳，秒）
- `endTime`: 结束时间（Unix时间戳，秒）
- `teamId`: 团队ID（可选）
- `dsMobile`: 分销员手机号（可选）
- `orderNo`: 订单号（可选）
- `groupId`: 分组ID（可选）
- `realKdtId`: 店铺ID（可选）
- `shopChannel`: 店铺渠道（-1=全部）
- `settleState`: 结算状态（可选）

### 请求头
- `accept`: application/json, text/plain, */*
- `cookie`: 包含认证信息的Cookie字符串
- `user-agent`: 浏览器标识

## 配置步骤

### 1. 获取Cookie

1. 使用Chrome浏览器登录有赞后台：https://www.youzan.com
2. 打开开发者工具（F12）
3. 访问订单列表页面
4. 在Network标签中找到 `getList.json` 请求
5. 复制该请求的完整Cookie值

### 2. 配置数据库

在数据库的 `t_config` 表中更新或插入Cookie配置：

```sql
INSERT INTO t_config (config_key, config_value, config_description)
VALUES ('shop_token', '你的完整Cookie字符串', '店铺Token')
ON DUPLICATE KEY UPDATE config_value = '你的完整Cookie字符串';
```

Cookie示例格式（注意：需要替换为真实的Cookie）：
```
yz_log_ftime=...; yz_log_uuid=...; KDTSESSIONID=...; sid=...; user_id=...; access_token=...; kdt_id=...
```

### 3. 重要Cookie字段说明

关键的认证字段包括：
- `KDTSESSIONID`: 会话ID
- `sid`: 会话标识
- `user_id`: 用户ID
- `access_token`: 访问令牌
- `kdt_id`: 店铺ID
- `team_auth_key`: 团队认证密钥

## 数据映射

### 有赞订单字段 → 系统订单字段

| 有赞字段 | 系统字段 | 说明 |
|---------|---------|------|
| orderNo | orderId | 订单编号 |
| dsUid | distributorId | 分销员ID |
| dsNickName | distributorName | 分销员名称 |
| money | orderAmount | 订单金额 |
| state | orderStatus | 订单状态 |
| createTime | orderDate | 订单创建时间 |
| customerNickname | customerName | 客户名称 |
| customerMobile | customerId | 客户手机号 |

### 订单状态映射

| 有赞状态码 | 系统状态 | 说明 |
|----------|---------|------|
| 1 | PENDING_PAYMENT | 待付款 |
| 2 | PENDING_SHIPMENT | 待发货 |
| 3 | SHIPPED | 已发货 |
| 4 | COMPLETED | 已完成 |
| 5 | CLOSED | 已关闭 |

## 使用方法

### 手动触发数据抓取

可以通过调用 `DataFetchService.fetchOrderData()` 方法手动触发订单数据抓取。

### 自动定时任务

系统配置了定时任务，会自动抓取订单数据。配置在 `application.yml` 中：

```yaml
scheduled:
  fetch-order-data:
    cron: "0 0 * * * ?"  # 每小时执行一次（每小时的0分0秒）
```

## 注意事项

1. **Cookie过期**：有赞的Cookie会定期过期，需要重新获取并更新配置
2. **请求频率**：代码中已加入了分页间隔（1秒），避免请求过快
3. **数据去重**：系统会根据 `orderId` 自动去重，已存在的订单会被更新
4. **时间范围**：默认抓取前24小时的订单数据
5. **分页限制**：为防止无限循环，最多获取100页数据

## 错误处理

如果遇到以下错误：

### API返回错误
- 检查Cookie是否过期
- 确认账号权限是否正常
- 检查网络连接

### 数据格式错误
- 确认有赞API响应格式是否变化
- 查看日志中的详细错误信息

### 分销员不存在
- 系统会自动创建不存在的分销员记录

## 日志

相关日志会输出到应用日志中，可以通过以下关键字搜索：
- "开始从有赞API获取订单数据"
- "请求第X页订单数据"
- "第X页获取到Y条订单"
- "共获取到X条订单数据"

## 示例响应

参考 `backend/src/main/resources/example/order.json` 文件查看完整的API响应示例。
