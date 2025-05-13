package com.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.dto.OrderDTO;
import com.store.dto.OrderFilter;
import com.store.entity.Order.OrderStatus;
import com.store.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO sampleOrderDTO;
    private final Long ORDER_ID = 1L;

    @BeforeEach
    public void setup() {
        sampleOrderDTO = new OrderDTO();
        sampleOrderDTO.setId(ORDER_ID);
        // Set other required properties for a valid OrderDTO
    }

    @Test
    @WithMockUser
    public void getOrders_withNoFilters_shouldReturnAllOrders() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderDTO> orderList = Collections.singletonList(sampleOrderDTO);
        PageImpl<OrderDTO> orderPage = new PageImpl<>(orderList, pageable, orderList.size());
        
        when(orderService.findOrdersByFilter(any(OrderFilter.class), any(Pageable.class)))
            .thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ORDER_ID));
        
        verify(orderService).findOrdersByFilter(any(OrderFilter.class), any(Pageable.class));
    }

    @Test
    @WithMockUser
    public void getOrders_withId_shouldReturnSpecificOrder() throws Exception {
        // Arrange
        when(orderService.findOrderById(ORDER_ID)).thenReturn(sampleOrderDTO);

        // Act & Assert
        mockMvc.perform(get("/api/orders")
                .param("id", ORDER_ID.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ORDER_ID));
        
        verify(orderService).findOrderById(ORDER_ID);
    }

    @Test
    @WithMockUser
    public void createOrder_withValidData_shouldReturnCreatedOrder() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(sampleOrderDTO);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleOrderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ORDER_ID));
        
        verify(orderService).createOrder(any(OrderDTO.class));
    }

    @Test
    @WithMockUser
    public void updateOrder_withValidData_shouldReturnUpdatedOrder() throws Exception {
        // Arrange
        when(orderService.updateOrder(any(OrderDTO.class))).thenReturn(sampleOrderDTO);

        // Act & Assert
        mockMvc.perform(put("/api/orders/{id}", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID));
        
        verify(orderService).updateOrder(any(OrderDTO.class));
    }

    @Test
    @WithMockUser
    public void updateOrderStatus_withValidStatus_shouldReturnUpdatedOrder() throws Exception {
        // Arrange
        OrderStatus newStatus = OrderStatus.SHIPPED;
        when(orderService.updateOrderStatus(eq(ORDER_ID), eq(newStatus))).thenReturn(sampleOrderDTO);

        // Act & Assert
        mockMvc.perform(put("/api/orders/{id}/status", ORDER_ID)
                .param("status", newStatus.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID));
        
        verify(orderService).updateOrderStatus(ORDER_ID, newStatus);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteOrder_asAdmin_shouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteOrder(ORDER_ID);

        // Act & Assert
        mockMvc.perform(delete("/api/orders/{id}", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(orderService).deleteOrder(ORDER_ID);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteOrder_asUser_shouldBeForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/orders/{id}", ORDER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        
        verify(orderService, never()).deleteOrder(any());
    }
}