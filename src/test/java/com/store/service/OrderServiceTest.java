package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.entity.OrderItem;
import com.store.entity.Product;
import com.store.entity.User;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Order testOrder;
    private OrderDTO testOrderDTO;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup authentication
        Set<String> roles = new HashSet<>();
        roles.add("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(roles);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Setup product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("49.99"));
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);

        // Setup order item
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setPrice(testProduct.getPrice());
        orderItem.setProductName(testProduct.getName());

        // Setup order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setShippingAddress("123 Test Street");
        testOrder.setItems(Collections.singletonList(orderItem));

        // Setup order DTO
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setUserId(1L);
        testOrderDTO.setStatus(OrderStatus.PENDING.name());
        testOrderDTO.setShippingAddress("123 Test Street");

        List<OrderDTO.OrderItemDTO> itemDTOs = new ArrayList<>();
        OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
        itemDTO.setId(1L);
        itemDTO.setProductId(1L);
        itemDTO.setProductName("Test Product");
        itemDTO.setQuantity(2);
        itemDTO.setPrice(new BigDecimal("49.99"));
        itemDTOs.add(itemDTO);

        testOrderDTO.setItems(itemDTOs);
    }

    @Test
    void findAllOrders_ShouldReturnAllOrders() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderDTO);

        // Act
        Page<OrderDTO> result = orderService.findAllOrders(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrderDTO, result.getContent().get(0));
        verify(orderRepository, times(1)).findAll(any(Pageable.class));
        verify(orderMapper, times(1)).toDTO(testOrder);
    }

    @Test
    void findOrderById_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.findOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderDTO, result);
        verify(orderRepository, times(1)).findById(1L);
        verify(orderMapper, times(1)).toDTO(testOrder);
    }

    @Test
    void findCurrentUserOrders_ShouldReturnUserOrders() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderDTO);

        // Act
        Page<OrderDTO> result = orderService.findCurrentUserOrders(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrderDTO, result.getContent().get(0));
        verify(orderRepository, times(1)).findByUserId(anyLong(), any(Pageable.class));
        verify(orderMapper, times(1)).toDTO(testOrder);
    }

    @Test
    void findOrdersByStatus_ShouldReturnOrdersWithStatus() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(orderRepository.findByStatus(OrderStatus.PENDING, Pageable.unpaged())).thenReturn(orderPage);
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderDTO);

        // Act
        Page<OrderDTO> result = orderService.findOrdersByStatus(OrderStatus.PENDING, Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrderDTO, result.getContent().get(0));
        verify(orderRepository, times(1)).findByStatus(OrderStatus.PENDING, Pageable.unpaged());
        verify(orderMapper, times(1)).toDTO(testOrder);
    }
}