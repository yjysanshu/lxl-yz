package com.lxl.yz.service;

import com.lxl.yz.dto.DailyReportDTO;
import com.lxl.yz.dto.MonthlyReportDTO;
import com.lxl.yz.dto.WeeklyReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    public List<DailyReportDTO> getDailyReport(LocalDate date, Long teamId) {
        log.info("查询每日报表，日期：{}，团队ID：{}", date, teamId);
        
        String sql = """
            SELECT 
                d.distributor_name,
                ds.order_count,
                ds.total_amount,
                ROUND(ds.total_amount / NULLIF(ds.order_count, 0), 2) as avg_price
            FROM t_daily_statistics ds
            LEFT JOIN t_distributor d ON ds.distributor_id = d.distributor_id
            WHERE ds.stat_date = ?
            """;
        
        if (teamId != null) {
            sql += " AND d.team_id = ?";
        }
        
        sql += " ORDER BY ds.total_amount DESC";
        
        List<Map<String, Object>> results;
        if (teamId != null) {
            results = jdbcTemplate.queryForList(sql, date, teamId);
        } else {
            results = jdbcTemplate.queryForList(sql, date);
        }
        
        List<DailyReportDTO> reports = new ArrayList<>();
        for (Map<String, Object> row : results) {
            DailyReportDTO dto = new DailyReportDTO();
            dto.setDistributorName((String) row.get("distributor_name"));
            dto.setOrderCount(((Number) row.get("order_count")).intValue());
            dto.setTotalAmount((BigDecimal) row.get("total_amount"));
            dto.setAvgPrice((BigDecimal) row.get("avg_price"));
            reports.add(dto);
        }
        
        return reports;
    }

    public List<MonthlyReportDTO> getMonthlyReport(String yearMonth, Long teamId) {
        log.info("查询月度报表，年月：{}，团队ID：{}", yearMonth, teamId);
        
        String sql = """
            SELECT 
                d.distributor_name,
                COUNT(DISTINCT o.customer_id) as customer_count,
                COUNT(*) as order_count,
                SUM(o.order_amount) as total_amount,
                ROUND(SUM(o.order_amount) / NULLIF(COUNT(*), 0), 2) as avg_price
            FROM t_order o
            LEFT JOIN t_distributor d ON o.distributor_id = d.distributor_id
            WHERE o.order_status = '已支付'
                AND DATE_FORMAT(o.order_date, '%Y-%m') = ?
            """;
        
        if (teamId != null) {
            sql += " AND d.team_id = ?";
        }
        
        sql += " GROUP BY o.distributor_id, d.distributor_name ORDER BY total_amount DESC";
        
        List<Map<String, Object>> results;
        if (teamId != null) {
            results = jdbcTemplate.queryForList(sql, yearMonth, teamId);
        } else {
            results = jdbcTemplate.queryForList(sql, yearMonth);
        }
        
        List<MonthlyReportDTO> reports = new ArrayList<>();
        for (Map<String, Object> row : results) {
            MonthlyReportDTO dto = new MonthlyReportDTO();
            dto.setDistributorName((String) row.get("distributor_name"));
            dto.setCustomerCount(((Number) row.get("customer_count")).longValue());
            dto.setOrderCount(((Number) row.get("order_count")).longValue());
            dto.setTotalAmount((BigDecimal) row.get("total_amount"));
            dto.setAvgPrice((BigDecimal) row.get("avg_price"));
            reports.add(dto);
        }
        
        return reports;
    }

    public List<WeeklyReportDTO> getWeeklyReport(Integer year, Integer week, Long teamId) {
        log.info("查询周度报表，年：{}，周：{}，团队ID：{}", year, week, teamId);
        
        // 计算该周的开始和结束日期
        WeekFields weekFields = WeekFields.of(Locale.CHINA);
        LocalDate startOfWeek = LocalDate.of(year, 1, 1)
            .with(weekFields.weekOfYear(), week)
            .with(weekFields.dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        String sql = """
            SELECT 
                d.distributor_name,
                COUNT(DISTINCT o.customer_id) as customer_count,
                COUNT(*) as order_count,
                SUM(o.order_amount) as total_amount,
                ROUND(SUM(o.order_amount) / NULLIF(COUNT(*), 0), 2) as avg_price
            FROM t_order o
            LEFT JOIN t_distributor d ON o.distributor_id = d.distributor_id
            WHERE o.order_status = '已支付'
                AND DATE(o.order_date) BETWEEN ? AND ?
            """;
        
        if (teamId != null) {
            sql += " AND d.team_id = ?";
        }
        
        sql += " GROUP BY o.distributor_id, d.distributor_name ORDER BY total_amount DESC";
        
        List<Map<String, Object>> results;
        if (teamId != null) {
            results = jdbcTemplate.queryForList(sql, startOfWeek, endOfWeek, teamId);
        } else {
            results = jdbcTemplate.queryForList(sql, startOfWeek, endOfWeek);
        }
        
        List<WeeklyReportDTO> reports = new ArrayList<>();
        for (Map<String, Object> row : results) {
            WeeklyReportDTO dto = new WeeklyReportDTO();
            dto.setDistributorName((String) row.get("distributor_name"));
            dto.setCustomerCount(((Number) row.get("customer_count")).longValue());
            dto.setOrderCount(((Number) row.get("order_count")).longValue());
            dto.setTotalAmount((BigDecimal) row.get("total_amount"));
            dto.setAvgPrice((BigDecimal) row.get("avg_price"));
            reports.add(dto);
        }
        
        return reports;
    }
}
