package com.store.controller;

import com.store.dto.OrderDTO;
import com.store.dto.OrderFilterDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;
    private OrderFilterDTO filterDTO;
    private Page<OrderDTO> orderPage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setStatus(String.valueOf(OrderStatus.PENDING));
        orderDTO.setTotalAmount(new BigDecimal("100.00"));

        filterDTO = new OrderFilterDTO();
        filterDTO.setStatus(OrderStatus.PENDING);

        pageable = PageRequest.of(0, 10);
        orderPage = new PageImpl<>(Collections.singletonList(orderDTO));
    }

    @Test
    void getOrders_ShouldReturnOrderPage() {
        when(orderService.findOrders(any(OrderFilterDTO.class), any(Pageable.class)))
                .thenReturn(orderPage);

        ResponseEntity<Page<OrderDTO>> response = orderController.getOrders(filterDTO, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(orderService).findOrders(filterDTO, pageable);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO.getId(), response.getBody().getId());
        verify(orderService).createOrder(orderDTO);
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        when(orderService.updateOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(1L, orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO.getId(), response.getBody().getId());
        verify(orderService).updateOrder(orderDTO);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderDTO.getId(), response.getBody().getId());
        verify(orderService).updateOrderStatus(1L, OrderStatus.PROCESSING);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        doNothing().when(orderService).deleteOrder(anyLong());

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).deleteOrder(1L);
    }
}