package com.store.controller;

import com.store.dto.OrderDTO;
import com.store.dto.OrderFilterDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.exceptions.OrderNotFoundException;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.service.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderCreationService orderCreationService;

    @Mock
    private OrderUpdateService orderUpdateService;

    @Mock
    private OrderAuthorizationService orderAuthorizationService;

    @Mock
    private OrderStatusService orderStatusService;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDTO orderDTO;
    private OrderFilterDTO filterDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setStatus(String.valueOf(OrderStatus.PENDING));
        orderDTO.setTotalAmount(new BigDecimal("100.00"));

        filterDTO = new OrderFilterDTO();
        filterDTO.setStatus(OrderStatus.PENDING);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findOrders_WithId_ShouldReturnSingleOrder() {
        filterDTO.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);
        doNothing().when(orderAuthorizationService).checkOrderAccess(any());

        Page<OrderDTO> result = orderService.findOrders(filterDTO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderDTO, result.getContent().get(0));
    }

    @Test
    void findOrders_WithFilters_ShouldReturnFilteredOrders() {
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findOrdersByFilters(
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(orderPage);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        Page<OrderDTO> result = orderService.findOrders(filterDTO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderDTO, result.getContent().get(0));
    }

    @Test
    void findOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);
        doNothing().when(orderAuthorizationService).checkOrderAccess(any());

        OrderDTO result = orderService.findOrderById(1L);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
    }

    @Test
    void findOrderById_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(1L));
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderCreationService.createOrder(any())).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        when(orderUpdateService.updateOrder(any())).thenReturn(orderDTO);

        OrderDTO result = orderService.updateOrder(orderDTO);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        when(orderStatusService.updateOrderStatus(anyLong(), any())).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertNotNull(result);
        assertEquals(orderDTO.getId(), result.getId());
    }

    @Test
    void deleteOrder_WhenOrderExists_ShouldDelete() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_WhenOrderNotFound_ShouldThrowException() {
        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, never()).deleteById(anyLong());
    }
}