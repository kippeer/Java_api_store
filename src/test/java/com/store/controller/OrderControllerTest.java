package com.store.controller;

import com.store.dto.OrderDTO;
import com.store.dto.OrderDTO.OrderItemDTO;
import com.store.entity.Order.OrderStatus;
import com.store.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    private OrderDTO mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockOrder = new OrderDTO();
        mockOrder.setId(1L);
        mockOrder.setUserId(1L);
        mockOrder.setStatus(OrderStatus.PENDING.name());
        mockOrder.setTotalAmount(BigDecimal.valueOf(100.0));
        mockOrder.setShippingAddress("123 Main St");
        mockOrder.setItems(List.of(new OrderItemDTO(1L, 1L, "Product A", 2, BigDecimal.valueOf(50.0))));
        mockOrder.setCreatedAt(LocalDateTime.now());
        mockOrder.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnOrderById() {
        when(orderService.findOrderById(1L)).thenReturn(mockOrder);

        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<?> response = orderController.getOrders(1L, null, null, null, null, null, false, pageable);

        assertEquals(200, response.getStatusCodeValue());
        Page<OrderDTO> page = (Page<OrderDTO>) response.getBody();
        assertEquals(1, page.getContent().size());
        assertEquals(mockOrder.getId(), page.getContent().get(0).getId());
    }

    @Test
    void shouldCreateOrder() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.createOrder(mockOrder);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockOrder.getId(), response.getBody().getId());
    }

    @Test
    void shouldUpdateOrderStatus() {
        when(orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)).thenReturn(mockOrder);

        ResponseEntity<OrderDTO> response = orderController.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockOrder.getStatus(), response.getBody().getStatus());
    }

    @Test
    void shouldDeleteOrder() {
        doNothing().when(orderService).deleteOrder(1L);

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(orderService, times(1)).deleteOrder(1L);
    }
}
