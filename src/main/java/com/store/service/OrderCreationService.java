package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.OrderItem;
import com.store.entity.Product;
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

    public Order createOrder(OrderDTO orderDTO) {
        Order order = initializeOrder(orderDTO);
        List<OrderItem> orderItems = createOrderItems(orderDTO, order);
        order.setItems(orderItems);
        order.recalculateTotal();
        return orderRepository.save(order);
    }

    private Order initializeOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUser(orderAuthorizationService.getCurrentUser());
        order.setShippingAddress(orderDTO.getShippingAddress());
        
        if (orderDTO.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(orderDTO.getStatus()));
        }
        
        if (orderDTO.getPaymentMethod() != null) {
            order.setPaymentMethod(Order.PaymentMethod.valueOf(orderDTO.getPaymentMethod()));
        }

        order.setPaymentReference(orderDTO.getPaymentReference());
        order.setShippingCost(orderDTO.getShippingCost() != null ? orderDTO.getShippingCost() : BigDecimal.ZERO);
        order.setTaxAmount(orderDTO.getTaxAmount() != null ? orderDTO.getTaxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        order.setTrackingNumber(orderDTO.getTrackingNumber());

        return order;
    }

    private List<OrderItem> createOrderItems(OrderDTO orderDTO, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productStockService.getAndValidateProduct(itemDTO.getProductId(), itemDTO.getQuantity());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setProductName(product.getName());
            
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }
}