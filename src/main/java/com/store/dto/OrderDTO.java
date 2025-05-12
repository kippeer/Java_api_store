package com.store.dto;

import com.store.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    private Long userId;

    private String status;

    private BigDecimal totalAmount;

    private BigDecimal shippingCost;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private String paymentMethod;

    private String paymentReference;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String trackingNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {

        private Long id;

        @NotNull(message = "Product ID is required")
        private Long productId;

        private String productName;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private BigDecimal price;
    }
}