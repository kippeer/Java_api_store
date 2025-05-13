package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderUpdateService {
    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;

    public Order updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderDTO.getId()));

        orderAuthorizationService.checkOrderUpdatePermission(order);
        
        updateOrderFields(order, orderDTO);
        order.recalculateTotal();
        
        return orderRepository.save(order);
    }

    private void updateOrderFields(Order order, OrderDTO orderDTO) {
        if (orderDTO.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(orderDTO.getStatus()));
        }

        if (orderDTO.getPaymentMethod() != null) {
            order.setPaymentMethod(Order.PaymentMethod.valueOf(orderDTO.getPaymentMethod()));
        }

        order.setPaymentReference(orderDTO.getPaymentReference());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setShippingCost(orderDTO.getShippingCost() != null ? orderDTO.getShippingCost() : order.getShippingCost());
        order.setTaxAmount(orderDTO.getTaxAmount() != null ? orderDTO.getTaxAmount() : order.getTaxAmount());
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : order.getDiscountAmount());
        order.setTrackingNumber(orderDTO.getTrackingNumber());
    }
}