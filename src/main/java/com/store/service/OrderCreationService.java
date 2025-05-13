package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Product;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderCreationService {
    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;
    private final ProductStockService productStockService;
    private final OrderMapper orderMapper;

    public Order createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO); // simples e direto
        List<OrderItem> items = createOrderItems(orderDTO, order);
        order.setItems(items);
        order.recalculateTotal();
        return orderRepository.save(order);
    }
    private Order mapOrderFromDTO(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO);
        order.setUser(orderAuthorizationService.getCurrentUser());
        return order;
    }

    private List<OrderItem> createOrderItems(OrderDTO orderDTO, Order order) {
        return orderDTO.getItems().stream()
                .map(itemDTO -> createOrderItem(itemDTO, order))
                .toList(); // Java 16+ simplificado
    }

    private OrderItem createOrderItem(OrderDTO.OrderItemDTO itemDTO, Order order) {
        Product product = productStockService.getAndValidateProduct(
                itemDTO.getProductId(),
                itemDTO.getQuantity()
        );

        return orderMapper.toOrderItem(itemDTO, order, product);
    }

}