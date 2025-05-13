package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.exceptions.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderCreationService orderCreationService;
    private final OrderUpdateService orderUpdateService;
    private final OrderAuthorizationService orderAuthorizationService;
    private final OrderStatusService orderStatusService;

    // Método genérico para buscar uma ordem por ID
    private Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO findOrderById(Long id) {
        Order order = getOrderById(id);
        orderAuthorizationService.checkOrderAccess(order);
        return orderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findCurrentUserOrders(Pageable pageable) {
        return orderRepository.findByUserId(orderAuthorizationService.getCurrentUser().getId(), pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByDateRange(startDate, endDate, pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrdersByAmountRange(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable) {
        return orderRepository.findByTotalAmountRange(minAmount, maxAmount, pageable)
                .map(orderMapper::toDTO);
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
            throw new OrderNotFoundException( id);
        }
        orderRepository.deleteById(id);
    }
}
