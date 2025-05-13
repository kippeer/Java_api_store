package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.exceptions.OrderNotFoundException;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderUpdateService {
    private final OrderRepository orderRepository;
    private final OrderAuthorizationService orderAuthorizationService;
    private final OrderMapper orderMapper;

    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new OrderNotFoundException(orderDTO.getId()));

        orderAuthorizationService.checkOrderUpdatePermission(order);

        orderMapper.updateOrderFromDTO(orderDTO, order);

        order.recalculateTotal();

        return orderMapper.toDTO(orderRepository.save(order));
    }
}
