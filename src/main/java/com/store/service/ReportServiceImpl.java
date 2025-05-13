package com.store.service;

import com.store.dto.ReportDTO.OrderStatusReportDTO;
import com.store.dto.ReportDTO.SalesReportDTO;
import com.store.entity.Order;
import com.store.mapper.OrderReportMapper;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final OrderRepository orderRepository;
    private final OrderReportMapper orderReportMapper;

    @Override
    public SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByDateRangeAndStatus(
                startDate,
                endDate,
                Order.OrderStatus.DELIVERED
        );
        return orderReportMapper.toSalesReportDTO(orders, startDate, endDate);
    }

    @Override
    public OrderStatusReportDTO generateOrderStatusReport() {
        List<Order> allOrders = orderRepository.findAll();
        return orderReportMapper.toOrderStatusReportDTO(allOrders);
    }
}