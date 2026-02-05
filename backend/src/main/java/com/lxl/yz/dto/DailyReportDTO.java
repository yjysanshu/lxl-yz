package com.lxl.yz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportDTO {
    private String distributorName;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private BigDecimal avgPrice;
}
