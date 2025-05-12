package com.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ReportDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesReportDTO {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal totalSales;
        private int totalOrders;
        private BigDecimal averageOrderValue;
        private Map<String, BigDecimal> salesByPaymentMethod;
        private List<DailySalesDTO> dailySales;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySalesDTO {
        private LocalDateTime date;
        private BigDecimal totalSales;
        private int orderCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusReportDTO {
        private Map<String, Integer> orderCountByStatus;
        private Map<String, BigDecimal> totalValueByStatus;
        private int totalOrders;
        private BigDecimal totalValue;
    }
}