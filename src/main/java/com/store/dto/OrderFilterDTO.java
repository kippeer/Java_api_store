package com.store.dto;

import com.store.entity.Order.OrderStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderFilterDTO {
    private Long id;
    private OrderStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private boolean currentUserOnly;
}