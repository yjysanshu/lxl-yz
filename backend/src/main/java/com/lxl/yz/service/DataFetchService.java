package com.lxl.yz.service;

import com.lxl.yz.entity.Config;
import com.lxl.yz.entity.Distributor;
import com.lxl.yz.entity.Order;
import com.lxl.yz.repository.ConfigRepository;
import com.lxl.yz.repository.DistributorRepository;
import com.lxl.yz.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataFetchService {

    // API配置常量
    private static final String YOUZAN_API_BASE_URL = "https://www.youzan.com/v4/ump/new-salesman/order/getList.json";
    private static final int PAGE_SIZE = 20;
    private static final int MAX_PAGES = 100;
    private static final long PAGE_DELAY_MS = 1000L;
    private static final int TIME_TYPE_CREATE_TIME = 1;
    private static final int SHOP_CHANNEL_ALL = -1;
    
    private final OrderRepository orderRepository;
    private final ConfigRepository configRepository;
    private final DistributorRepository distributorRepository;
    private final DailyStatisticsService dailyStatisticsService;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void fetchOrderData() {
        log.info("开始抓取订单数据...");
        
        // 1. 获取店铺Token
        Optional<Config> configOpt = configRepository.findByConfigKey("shop_token");
        if (configOpt.isEmpty()) {
            log.error("未找到店铺Token配置");
            return;
        }
        
        String shopToken = configOpt.get().getConfigValue();
        
        // 2. 调用第三方API获取订单数据 (这里是模拟示例)
        List<Map<String, Object>> orderDataList = fetchFromThirdPartyAPI(shopToken);
        
        // 3. 处理订单数据
        int newCount = 0;
        int updateCount = 0;
        
        for (Map<String, Object> orderData : orderDataList) {
            String orderId = (String) orderData.get("orderId");
            
            Optional<Order> existingOrder = orderRepository.findByOrderId(orderId);
            
            Order order;
            if (existingOrder.isPresent()) {
                // 更新现有订单
                order = existingOrder.get();
                updateCount++;
            } else {
                // 创建新订单
                order = new Order();
                order.setOrderId(orderId);
                newCount++;
            }
            
            // 设置订单信息
            order.setDistributorId(((Number) orderData.get("distributorId")).longValue());
            order.setOrderAmount(new BigDecimal(orderData.get("orderAmount").toString()));
            order.setOrderStatus((String) orderData.get("orderStatus"));
            order.setOrderDate(LocalDateTime.parse(orderData.get("orderDate").toString()));
            order.setCustomerName((String) orderData.get("customerName"));
            order.setCustomerId((String) orderData.get("customerId"));
            
            // 确保分销员存在
            ensureDistributorExists(order.getDistributorId(), 
                (String) orderData.getOrDefault("distributorName", "未知分销员"));
            
            orderRepository.save(order);
        }
        
        log.info("订单数据抓取完成，新增：{}，更新：{}", newCount, updateCount);
        
        // 4. 触发每日统计计算
        dailyStatisticsService.calculateDailyStatistics();
    }
    
    private void ensureDistributorExists(Long distributorId, String distributorName) {
        if (!distributorRepository.existsById(distributorId)) {
            Distributor distributor = new Distributor();
            distributor.setDistributorId(distributorId);
            distributor.setDistributorName(distributorName);
            distributorRepository.save(distributor);
        }
    }
    
    /**
     * 调用第三方API获取订单数据
     * 从有赞API获取订单列表
     */
    private List<Map<String, Object>> fetchFromThirdPartyAPI(String token) {
        log.info("开始从有赞API获取订单数据，Token: {}", token);
        
        List<Map<String, Object>> allOrders = new ArrayList<>();
        
        try {
            // 构建WebClient
            WebClient webClient = webClientBuilder.build();
            
            // API基础URL和参数
            int currentPage = 1;
            boolean hasNext = true;
            
            // 获取时间范围 - 当天
            long currentTime = System.currentTimeMillis() / 1000;
            long startTime = currentTime - 86400; // 前一天
            long endTime = currentTime;
            
            while (hasNext) {
                String url = String.format("%s?pageSize=%d&page=%d&timeType=%d&startTime=%d&endTime=%d&teamId=&dsMobile=&orderNo=&groupId=&realKdtId=&shopChannel=%d&settleState=",
                    YOUZAN_API_BASE_URL, PAGE_SIZE, currentPage, TIME_TYPE_CREATE_TIME, startTime, endTime, SHOP_CHANNEL_ALL);
                
                log.info("请求第{}页订单数据: {}", currentPage, url);
                
                // 调用API
                Map<String, Object> response = webClient.get()
                    .uri(url)
                    .header("accept", "application/json, text/plain, */*")
                    .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("cookie", token) // token作为cookie使用
                    .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
                
                if (response == null) {
                    log.error("API响应为空");
                    break;
                }
                
                // 检查响应状态
                Integer code = (Integer) response.get("code");
                if (code == null || code != 0) {
                    log.error("API返回错误: {}", response.get("msg"));
                    break;
                }
                
                // 获取数据
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data == null) {
                    log.error("响应中没有data字段");
                    break;
                }
                
                List<Map<String, Object>> orderList = (List<Map<String, Object>>) data.get("list");
                if (orderList != null && !orderList.isEmpty()) {
                    // 转换订单数据格式
                    for (Map<String, Object> order : orderList) {
                        Map<String, Object> convertedOrder = convertOrderData(order);
                        allOrders.add(convertedOrder);
                    }
                    log.info("第{}页获取到{}条订单", currentPage, orderList.size());
                } else {
                    log.info("第{}页没有订单数据", currentPage);
                }
                
                // 检查是否有下一页
                Boolean hasNextPage = (Boolean) data.get("hasNext");
                hasNext = hasNextPage != null && hasNextPage;
                currentPage++;
                
                // 防止无限循环，最多获取100页
                if (currentPage > MAX_PAGES) {
                    log.warn("已达到最大页数限制({}页)，停止获取", MAX_PAGES);
                    break;
                }
                
                // 避免请求过快
                if (hasNext) {
                    Thread.sleep(PAGE_DELAY_MS);
                }
            }
            
            log.info("共获取到{}条订单数据", allOrders.size());
            
        } catch (Exception e) {
            log.error("获取订单数据失败", e);
        }
        
        return allOrders;
    }
    
    /**
     * 转换有赞订单数据为系统使用的格式
     */
    private Map<String, Object> convertOrderData(Map<String, Object> yzOrder) {
        Map<String, Object> order = new java.util.HashMap<>();
        
        // 订单编号
        order.put("orderId", yzOrder.get("orderNo"));
        
        // 分销员ID
        Object dsUid = yzOrder.get("dsUid");
        order.put("distributorId", dsUid != null ? ((Number) dsUid).longValue() : 0L);
        
        // 分销员名称
        order.put("distributorName", yzOrder.getOrDefault("dsNickName", "未知分销员"));
        
        // 订单金额 - 转换为BigDecimal
        String moneyStr = (String) yzOrder.getOrDefault("money", "0.00");
        try {
            BigDecimal orderAmount = new BigDecimal(moneyStr);
            order.put("orderAmount", orderAmount.toString());
        } catch (NumberFormatException e) {
            log.warn("订单金额格式错误: {}, 使用默认值0.00", moneyStr);
            order.put("orderAmount", "0.00");
        }
        
        // 订单状态 - state字段：1=待付款，2=待发货，3=已发货，4=已完成，5=已关闭
        Integer state = (Integer) yzOrder.get("state");
        String orderStatus = "UNKNOWN";
        if (state != null) {
            switch (state) {
                case 1: orderStatus = "PENDING_PAYMENT"; break;
                case 2: orderStatus = "PENDING_SHIPMENT"; break;
                case 3: orderStatus = "SHIPPED"; break;
                case 4: orderStatus = "COMPLETED"; break;
                case 5: orderStatus = "CLOSED"; break;
                default: orderStatus = "UNKNOWN";
            }
        }
        order.put("orderStatus", orderStatus);
        
        // 订单时间 - createTime是Unix时间戳（秒）
        Object createTime = yzOrder.get("createTime");
        if (createTime != null) {
            long timestamp = ((Number) createTime).longValue();
            LocalDateTime orderDate = LocalDateTime.ofEpochSecond(timestamp, 0, 
                java.time.ZoneOffset.ofHours(8)); // 东八区
            order.put("orderDate", orderDate.toString());
        } else {
            order.put("orderDate", LocalDateTime.now().toString());
        }
        
        // 客户信息
        order.put("customerName", yzOrder.getOrDefault("customerNickname", "未知客户"));
        order.put("customerId", yzOrder.getOrDefault("customerMobile", "unknown"));
        
        return order;
    }
}
