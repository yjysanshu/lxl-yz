package com.lxl.yz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReportDTO {
    private String distributorName;
    private Long customerCount;
    private Long orderCount;
    private BigDecimal totalAmount;
    private BigDecimal avgPrice;
}
