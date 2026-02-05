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
     * 调用第三方API获取订单数据（示例实现）
     * 实际项目中需要根据真实的第三方API进行调整
     */
    private List<Map<String, Object>> fetchFromThirdPartyAPI(String token) {
        // 这里是模拟数据，实际应该调用真实的第三方API
        // WebClient webClient = webClientBuilder.build();
        // return webClient.get()
        //     .uri("https://third-party-api.com/orders")
        //     .header("Authorization", "Bearer " + token)
        //     .retrieve()
        //     .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
        //     .block();
        
        log.info("模拟从第三方API获取订单数据，Token: {}", token);
        return new ArrayList<>(); // 返回空列表，实际应该返回真实数据
    }
}
