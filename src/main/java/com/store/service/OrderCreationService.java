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
        Order order = initializeOrder(orderDTO);
        List<OrderItem> orderItems = createOrderItems(orderDTO, order);
        order.setItems(orderItems);
        order.recalculateTotal();
        return orderRepository.save(order);
    }

    private Order initializeOrder(OrderDTO orderDTO) {
        // Mapeia o DTO diretamente para a entidade Order
        Order order = orderMapper.toOrder(orderDTO);

        // Define o usuário atual
        order.setUser(orderAuthorizationService.getCurrentUser());

        // Usando um método para lidar com valores nulos de forma mais limpa
        order.setShippingCost(getOrDefault(orderDTO.getShippingCost()));
        order.setTaxAmount(getOrDefault(orderDTO.getTaxAmount()));
        order.setDiscountAmount(getOrDefault(orderDTO.getDiscountAmount()));
        order.setTrackingNumber(orderDTO.getTrackingNumber());

        return order;
    }

    private List<OrderItem> createOrderItems(OrderDTO orderDTO, Order order) {
        // Usando Streams para criar a lista de OrderItems de forma mais concisa
        return orderDTO.getItems().stream()
                .map(itemDTO -> createOrderItem(itemDTO, order))
                .collect(Collectors.toList());
    }

    private OrderItem createOrderItem(OrderDTO.OrderItemDTO itemDTO, Order order) {
        // Valida o produto
        Product product = productStockService.getAndValidateProduct(itemDTO.getProductId(), itemDTO.getQuantity());

        // Mapeia o OrderItem usando o mapper
        OrderItem orderItem = orderMapper.toOrderItem(itemDTO);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());

        return orderItem;
    }

    private BigDecimal getOrDefault(BigDecimal value) {
        // Método para tratar valores nulos
        return value != null ? value : BigDecimal.ZERO;
    }
}
