package com.store.service;

import com.store.dto.ReportDTO.OrderStatusReportDTO;
import com.store.dto.ReportDTO.SalesReportDTO;
import java.time.LocalDateTime;

public interface ReportService {
    SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate);
    OrderStatusReportDTO generateOrderStatusReport();
}