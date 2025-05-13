package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Product;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCreationService {
    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;
    private final ProductStockService productStockService;
    private final OrderMapper orderMapper;  // Adicionando o mapper

    public Order createOrder(OrderDTO orderDTO) {
        Order order = initializeOrder(orderDTO);
        List<OrderItem> orderItems = createOrderItems(orderDTO, order);
        order.setItems(orderItems);
        order.recalculateTotal();
        return orderRepository.save(order);
    }

    private Order initializeOrder(OrderDTO orderDTO) {
        // Usando o OrderMapper para mapear o DTO para a entidade Order
        Order order = orderMapper.toOrder(orderDTO);

        order.setUser(orderAuthorizationService.getCurrentUser());

        order.setShippingCost(orderDTO.getShippingCost() != null ? orderDTO.getShippingCost() : BigDecimal.ZERO);
        order.setTaxAmount(orderDTO.getTaxAmount() != null ? orderDTO.getTaxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        order.setTrackingNumber(orderDTO.getTrackingNumber());

        return order;
    }

    private List<OrderItem> createOrderItems(OrderDTO orderDTO, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();

        // Utilizando o mapper para criar os itens do pedido
        for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productStockService.getAndValidateProduct(itemDTO.getProductId(), itemDTO.getQuantity());

            OrderItem orderItem = orderMapper.toOrderItem(itemDTO); // Usando o mapper para criar OrderItem a partir do DTO
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
        }

        return orderItems;
    }
}
