package com.store.controller;

import com.store.dto.OrderDTO;
import com.store.dto.OrderFilter;
import com.store.entity.Order.OrderStatus;
import com.store.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerParamTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void getOrders_withId_shouldReturnOrderWithId() {
        // Arrange
        Long orderId = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderId);
        OrderFilter filter = new OrderFilter();
        Pageable pageable = mock(Pageable.class);
        
        when(orderService.findOrderById(orderId)).thenReturn(orderDTO);

        // Act
        ResponseEntity<Page<OrderDTO>> response = orderController.getOrders(
            orderId, filter, pageable, null, null, null, false, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(orderId, response.getBody().getContent().get(0).getId());
        
        verify(orderService).findOrderById(orderId);
        verify(orderService, never()).findOrdersByFilter(any(), any());
    }

    @Test
    public void getOrders_withoutId_shouldUseFilterAndPageable() {
        // Arrange
        OrderFilter filter = new OrderFilter();
        Pageable pageable = mock(Pageable.class);
        
        PageImpl<OrderDTO> expectedPage = new PageImpl<>(
            Collections.singletonList(new OrderDTO()), pageable, 1);
        
        when(orderService.findOrdersByFilter(eq(filter), eq(pageable))).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<OrderDTO>> response = orderController.getOrders(
            null, filter, pageable, null, null, null, false, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        
        verify(orderService, never()).findOrderById(any());
        verify(orderService).findOrdersByFilter(eq(filter), eq(pageable));
    }

    @Test
    public void createOrder_shouldReturnCreatedOrder() {
        // Arrange
        OrderDTO inputOrderDTO = new OrderDTO();
        OrderDTO createdOrderDTO = new OrderDTO();
        createdOrderDTO.setId(1L);
        
        when(orderService.createOrder(inputOrderDTO)).thenReturn(createdOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.createOrder(inputOrderDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdOrderDTO, response.getBody());
        
        verify(orderService).createOrder(inputOrderDTO);
    }

    @Test
    public void updateOrder_shouldSetIdAndReturnUpdatedOrder() {
        // Arrange
        Long orderId = 1L;
        OrderDTO inputOrderDTO = new OrderDTO();
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(orderId);
        
        when(orderService.updateOrder(inputOrderDTO)).thenReturn(updatedOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.updateOrder(orderId, inputOrderDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrderDTO, response.getBody());
        assertEquals(orderId, inputOrderDTO.getId());
        
        verify(orderService).updateOrder(inputOrderDTO);
    }

    @Test
    public void updateOrderStatus_shouldReturnUpdatedOrder() {
        // Arrange
        Long orderId = 1L;
        OrderStatus newStatus = OrderStatus.SHIPPED;
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(orderId);
        
        when(orderService.updateOrderStatus(orderId, newStatus)).thenReturn(updatedOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.updateOrderStatus(orderId, newStatus);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedOrderDTO, response.getBody());
        
        verify(orderService).updateOrderStatus(orderId, newStatus);
    }

    @Test
    public void deleteOrder_shouldReturnNoContent() {
        // Arrange
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(orderService).deleteOrder(orderId);
    }
}