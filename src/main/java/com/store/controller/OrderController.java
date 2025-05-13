package com.store.controller;

import com.store.dto.OrderDTO;
import com.store.dto.OrderFilterDTO;
import com.store.entity.Order.OrderStatus;
import com.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get orders with filtering",
            description = "Returns orders based on provided filters. Admin can access all orders, regular users only see their own orders.")
    public ResponseEntity<Page<OrderDTO>> getOrders(
            @Valid OrderFilterDTO filterDTO,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.findOrders(filterDTO, pageable));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a new order")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Updates an existing order")
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO
    ) {
        orderDTO.setId(id);
        return ResponseEntity.ok(orderService.updateOrder(orderDTO));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete order", description = "Deletes an order by its ID")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id
    ) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}