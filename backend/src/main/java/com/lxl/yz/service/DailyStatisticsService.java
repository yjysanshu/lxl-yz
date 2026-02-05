package com.lxl.yz.service;

import com.lxl.yz.entity.DailyStatistics;
import com.lxl.yz.repository.DailyStatisticsRepository;
import com.lxl.yz.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DailyStatisticsService {

    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void calculateDailyStatistics() {
        log.info("开始计算每日统计数据...");
        
        LocalDate today = LocalDate.now();
        calculateDailyStatisticsForDate(today);
        
        log.info("每日统计数据计算完成");
    }
    
    @Transactional
    public void calculateDailyStatisticsForDate(LocalDate date) {
        log.info("计算日期 {} 的统计数据", date);
        
        String sql = """
            SELECT 
                distributor_id,
                COUNT(*) as order_count,
                SUM(order_amount) as total_amount
            FROM t_order
            WHERE order_status = '已支付'
                AND DATE(order_date) = ?
            GROUP BY distributor_id
            """;
        
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, date);
        
        for (Map<String, Object> row : results) {
            Long distributorId = ((Number) row.get("distributor_id")).longValue();
            Integer orderCount = ((Number) row.get("order_count")).intValue();
            BigDecimal totalAmount = (BigDecimal) row.get("total_amount");
            
            // 查找或创建统计记录
            DailyStatistics stats = dailyStatisticsRepository
                .findByDistributorIdAndStatDate(distributorId, date)
                .orElse(new DailyStatistics());
            
            stats.setDistributorId(distributorId);
            stats.setStatDate(date);
            stats.setOrderCount(orderCount);
            stats.setTotalAmount(totalAmount);
            
            dailyStatisticsRepository.save(stats);
        }
        
        log.info("日期 {} 的统计数据计算完成，共 {} 条", date, results.size());
    }
}
