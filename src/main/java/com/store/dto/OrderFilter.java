package com.store.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.store.entity.Order;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class OrderFilter {
    // Filtros existentes (mantidos como estão)
    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private Order.OrderStatus status;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @JsonIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @JsonIgnore
    private BigDecimal minAmount;

    @JsonIgnore
    private BigDecimal maxAmount;

    @JsonIgnore
    private Boolean onlyCurrentUser;

    // Novos filtros para produtos e itens
    @JsonIgnore
    private Long productId;

    @JsonIgnore
    private Integer minQuantity;

    @JsonIgnore
    private Integer maxQuantity;

    @JsonIgnore
    private BigDecimal minPrice;

    @JsonIgnore
    private BigDecimal maxPrice;
}