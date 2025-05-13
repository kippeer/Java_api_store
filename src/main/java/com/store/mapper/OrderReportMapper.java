package com.store.mapper;

import com.store.dto.ReportDTO.OrderStatusReportDTO;
import com.store.dto.ReportDTO.SalesReportDTO;
import com.store.entity.Order;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderReportMapper {
    SalesReportDTO toSalesReportDTO(List<Order> orders, LocalDateTime startDate, LocalDateTime endDate);
    OrderStatusReportDTO toOrderStatusReportDTO(List<Order> orders);
}