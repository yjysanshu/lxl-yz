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
     * 自动抓取订单数据
     * 执行时间通过配置文件中的 scheduled.fetch-order-data.cron 属性配置
     */
    @Scheduled(cron = "${scheduled.fetch-order-data.cron}")
    public void fetchOrderDataHourly() {
        log.info("定时任务：开始订单数据抓取");
        try {
            dataFetchService.fetchOrderData();
            log.info("定时任务：订单数据抓取完成");
        } catch (Exception e) {
            log.error("定时任务：订单数据抓取失败", e);
        }
    }

    /**
     * 计算每日统计数据
     * 执行时间通过配置文件中的 scheduled.calculate-daily-statistics.cron 属性配置
     */
    @Scheduled(cron = "${scheduled.calculate-daily-statistics.cron}")
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
