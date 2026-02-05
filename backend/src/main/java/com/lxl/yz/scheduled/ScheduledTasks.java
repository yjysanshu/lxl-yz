package com.lxl.yz.scheduled;

import com.lxl.yz.service.DataFetchService;
import com.lxl.yz.service.DailyStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {

    private final DataFetchService dataFetchService;
    private final DailyStatisticsService dailyStatisticsService;

    /**
     * 每小时自动抓取订单数据
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void fetchOrderDataHourly() {
        log.info("定时任务：开始每小时订单数据抓取");
        try {
            dataFetchService.fetchOrderData();
            log.info("定时任务：每小时订单数据抓取完成");
        } catch (Exception e) {
            log.error("定时任务：每小时订单数据抓取失败", e);
        }
    }

    /**
     * 每天凌晨1点计算每日统计数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void calculateDailyStatistics() {
        log.info("定时任务：开始计算每日统计数据");
        try {
            dailyStatisticsService.calculateDailyStatistics();
            log.info("定时任务：每日统计数据计算完成");
        } catch (Exception e) {
            log.error("定时任务：每日统计数据计算失败", e);
        }
    }
}
