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
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        User currentUser = orderAuthorizationService.getCurrentUser();

        // Validação separada
        validateStatusUpdate(order, status, currentUser);

        order.setStatus(status);
        return orderRepository.save(order);
    }

    private void validateStatusUpdate(Order order, OrderStatus newStatus, User currentUser) {
        boolean isAdmin = currentUser.getRoles().contains("ADMIN");

        if (!isAdmin) {
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new OrderAccessDeniedException("You don't have permission to update this order");
            }

            if (newStatus == OrderStatus.SHIPPED ||
                    newStatus == OrderStatus.DELIVERED ||
                    newStatus == OrderStatus.REFUNDED) {
                throw new OrderAccessDeniedException("You don't have permission to set this order status");
            }

            if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
                throw new InvalidOrderStatusException("You can only cancel pending orders");
            }
        }
    }
}
