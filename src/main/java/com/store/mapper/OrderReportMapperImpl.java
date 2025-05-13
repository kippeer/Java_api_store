package com.store.mapper;

import com.store.dto.ReportDTO.DailySalesDTO;
import com.store.dto.ReportDTO.OrderStatusReportDTO;
import com.store.dto.ReportDTO.SalesReportDTO;
import com.store.entity.Order;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderReportMapperImpl implements OrderReportMapper {

    @Override
    public SalesReportDTO toSalesReportDTO(List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        int orderCount = orders.size();
        BigDecimal totalSales = calculateTotalAmount(orders);
        BigDecimal averageOrderValue = calculateAverageOrderValue(totalSales, orderCount);
        Map<String, BigDecimal> salesByPaymentMethod = calculateSalesByPaymentMethod(orders);
        List<DailySalesDTO> dailySales = generateDailySales(orders, startDate, endDate);

        return new SalesReportDTO(
                startDate,
                endDate,
                totalSales,
                orderCount,
                averageOrderValue,
                salesByPaymentMethod,
                dailySales
        );
    }

    @Override
    public OrderStatusReportDTO toOrderStatusReportDTO(List<Order> orders) {
        Map<String, Integer> orderCountByStatus = calculateOrderCountByStatus(orders);
        Map<String, BigDecimal> totalValueByStatus = calculateTotalValueByStatus(orders);
        BigDecimal totalValue = calculateTotalAmount(orders);

        return new OrderStatusReportDTO(
                orderCountByStatus,
                totalValueByStatus,
                orders.size(),
                totalValue
        );
    }

    private BigDecimal calculateAverageOrderValue(BigDecimal totalSales, int orderCount) {
        return orderCount == 0 ? BigDecimal.ZERO :
                totalSales.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);
    }

    private Map<String, BigDecimal> calculateSalesByPaymentMethod(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getPaymentMethod().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));
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
            BigDecimal dailyTotal = calculateTotalAmount(dailyOrders);
            int dailyOrderCount = dailyOrders.size();

            dailySales.add(new DailySalesDTO(currentDate, dailyTotal, dailyOrderCount));
            currentDate = currentDate.plusDays(1);
        }

        return dailySales;
    }

    private Map<String, Integer> calculateOrderCountByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().toString(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }

    private Map<String, BigDecimal> calculateTotalValueByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getStatus().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalAmount, BigDecimal::add)
                ));
    }

    private BigDecimal calculateTotalAmount(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}