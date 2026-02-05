package com.lxl.yz.controller;

import com.lxl.yz.dto.ApiResponse;
import com.lxl.yz.dto.DailyReportDTO;
import com.lxl.yz.dto.MonthlyReportDTO;
import com.lxl.yz.dto.WeeklyReportDTO;
import com.lxl.yz.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@Slf4j
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ApiResponse<List<DailyReportDTO>> getDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long teamId) {
        try {
            List<DailyReportDTO> report = reportService.getDailyReport(date, teamId);
            return ApiResponse.success(report);
        } catch (Exception e) {
            log.error("查询每日报表失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/monthly")
    public ApiResponse<List<MonthlyReportDTO>> getMonthlyReport(
            @RequestParam String yearMonth,
            @RequestParam(required = false) Long teamId) {
        try {
            List<MonthlyReportDTO> report = reportService.getMonthlyReport(yearMonth, teamId);
            return ApiResponse.success(report);
        } catch (Exception e) {
            log.error("查询月度报表失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/weekly")
    public ApiResponse<List<WeeklyReportDTO>> getWeeklyReport(
            @RequestParam Integer year,
            @RequestParam Integer week,
            @RequestParam(required = false) Long teamId) {
        try {
            List<WeeklyReportDTO> report = reportService.getWeeklyReport(year, week, teamId);
            return ApiResponse.success(report);
        } catch (Exception e) {
            log.error("查询周度报表失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
}
