package com.store.service;

import com.store.dto.ReportDTO.OrderStatusReportDTO;
import com.store.dto.ReportDTO.SalesReportDTO;
import com.store.dto.ReportDTO.DailySalesDTO;
import com.store.entity.Order;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    public SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetweenAndStatus(
                startDate,
                endDate,
                Order.OrderStatus.DELIVERED
        );

        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderValue = orders.isEmpty() ? BigDecimal.ZERO :
                totalSales.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

        Map<String, BigDecimal> salesByPaymentMethod = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getPaymentMethod().toString(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Order::getTotalAmount,
                                BigDecimal::add
                        )
                ));

        List<DailySalesDTO> dailySales = generateDailySales(orders, startDate, endDate);

        return new SalesReportDTO(
                startDate,
                endDate,
                totalSales,
                orders.size(),
                averageOrderValue,
                salesByPaymentMethod,
                dailySales
        );
    }

    private List<DailySalesDTO> generateDailySales(List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        Map<LocalDateTime, List<Order>> ordersByDay = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().with(LocalTime.MIN)
                ));

        List<DailySalesDTO> dailySales = new ArrayList<>();
        LocalDateTime currentDate = startDate.with(LocalTime.MIN);

        while (!currentDate.isAfter(endDate)) {
            List<Order> dailyOrders = ordersByDay.getOrDefault(currentDate, Collections.emptyList());
            BigDecimal dailyTotal = dailyOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dailySales.add(new DailySalesDTO(
                    currentDate,
                    dailyTotal,
                    dailyOrders.size()
            ));

            currentDate = currentDate.plusDays(1);
        }

        return dailySales;
    }

    public OrderStatusReportDTO generateOrderStatusReport() {
        List<Order> allOrders = orderRepository.findAll();

        Map<String, Integer> orderCountByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().toString(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        Map<String, BigDecimal> totalValueByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().toString(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Order::getTotalAmount,
                                BigDecimal::add
                        )
                ));

        BigDecimal totalValue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderStatusReportDTO(
                orderCountByStatus,
                totalValueByStatus,
                allOrders.size(),
                totalValue
        );
    }
}