package com.lxl.yz.controller;

import com.lxl.yz.dto.ApiResponse;
import com.lxl.yz.service.DataFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
@Slf4j
@RequiredArgsConstructor
public class DataController {

    private final DataFetchService dataFetchService;

    @PostMapping("/fetch")
    public ApiResponse<String> fetchData() {
        try {
            log.info("手动触发数据抓取");
            dataFetchService.fetchOrderData();
            return ApiResponse.success("数据抓取完成");
        } catch (Exception e) {
            log.error("数据抓取失败", e);
            return ApiResponse.error("数据抓取失败：" + e.getMessage());
        }
    }
}
