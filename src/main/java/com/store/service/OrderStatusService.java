package com.store.service;

import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.entity.User;
import com.store.exceptions.InvalidOrderStatusException;
import com.store.exceptions.OrderAccessDeniedException;
import com.store.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        User currentUser = orderAuthorizationService.getCurrentUser();

        validateStatusUpdate(order, newStatus, currentUser);

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private void validateStatusUpdate(Order order, OrderStatus newStatus, User currentUser) {
        if (isAdmin(currentUser)) return;

        validateUserOwnership(order, currentUser);
        validateStatusPermission(newStatus);
        validatePendingCancellation(order, newStatus);
    }

    private boolean isAdmin(User user) {
        return user.getRoles().contains("ADMIN");
    }

    private void validateUserOwnership(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderAccessDeniedException("You don't have permission to update this order");
        }
    }

    private void validateStatusPermission(OrderStatus status) {
        switch (status) {
            case SHIPPED, DELIVERED, REFUNDED -> throw new OrderAccessDeniedException(
                    "You don't have permission to set this order status");
            default -> {
                // Permitido
            }
        }
    }

    private void validatePendingCancellation(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("You can only cancel pending orders");
        }
    }
}
