package com.store.service;

import com.store.dto.OrderDTO;
import com.store.dto.OrderFilterDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.exceptions.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderCreationService orderCreationService;
    private final OrderUpdateService orderUpdateService;
    private final OrderAuthorizationService orderAuthorizationService;
    private final OrderStatusService orderStatusService;

    private Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrders(OrderFilterDTO filterDTO, Pageable pageable) {
        // If specific order ID is requested
        if (filterDTO.getId() != null) {
            Order order = getOrderById(filterDTO.getId());
            orderAuthorizationService.checkOrderAccess(order);
            OrderDTO orderDTO = orderMapper.toDTO(order);
            return new PageImpl<>(Collections.singletonList(orderDTO), pageable, 1);
        }

        // Get current user ID if needed
        Long userId = filterDTO.isCurrentUserOnly() ?
                orderAuthorizationService.getCurrentUser().getId() : null;

        return orderRepository.findOrdersByFilters(
                filterDTO.getStartDate(),
                filterDTO.getEndDate(),
                filterDTO.getStatus(),
                filterDTO.getMinAmount(),
                filterDTO.getMaxAmount(),
                userId,
                pageable
        ).map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO findOrderById(Long id) {
        Order order = getOrderById(id);
        orderAuthorizationService.checkOrderAccess(order);
        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        return orderMapper.toDTO(orderCreationService.createOrder(orderDTO));
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        return orderUpdateService.updateOrder(orderDTO);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        return orderMapper.toDTO(orderStatusService.updateOrderStatus(id, status));
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }
}