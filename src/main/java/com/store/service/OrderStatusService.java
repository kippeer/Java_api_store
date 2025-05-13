package com.store.service;

import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.entity.User;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderStatusService {
    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        User currentUser = orderAuthorizationService.getCurrentUser();
        validateStatusUpdate(order, status, currentUser);
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private void validateStatusUpdate(Order order, OrderStatus newStatus, User currentUser) {
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");
        
        if (!isAdmin && (newStatus == OrderStatus.SHIPPED || 
                        newStatus == OrderStatus.DELIVERED || 
                        newStatus == OrderStatus.REFUNDED)) {
            throw new AccessDeniedException("You don't have permission to update this order status");
        }

        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        if (!isAdmin && newStatus == OrderStatus.CANCELLED && 
            order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("You can only cancel pending orders");
        }
    }
}