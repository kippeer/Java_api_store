package com.store.service;

import com.store.dto.OrderDTO;
import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import com.store.entity.OrderItem;
import com.store.entity.Product;
import com.store.entity.User;
import com.store.mapper.OrderMapper;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO findOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the current user is the owner of the order or an admin
        if (!currentUser.getRoles().contains("ADMIN") && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to access this order");
        }

        return orderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findCurrentUserOrders(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(currentUser.getId(), pageable)
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(currentUser);
        order.setShippingAddress(orderDTO.getShippingAddress());

        if (orderDTO.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        }

        if (orderDTO.getPaymentMethod() != null) {
            order.setPaymentMethod(Order.PaymentMethod.valueOf(orderDTO.getPaymentMethod()));
        }

        order.setPaymentReference(orderDTO.getPaymentReference());
        order.setShippingCost(orderDTO.getShippingCost() != null ? orderDTO.getShippingCost() : BigDecimal.ZERO);
        order.setTaxAmount(orderDTO.getTaxAmount() != null ? orderDTO.getTaxAmount() : BigDecimal.ZERO);
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        order.setTrackingNumber(orderDTO.getTrackingNumber());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + itemDTO.getProductId()));

            // Check if product is active and has enough stock
            if (!product.getActive()) {
                throw new RuntimeException("Product is not available: " + product.getName());
            }

            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setProductName(product.getName());

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.recalculateTotal();

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderDTO.getId()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the current user is the owner of the order or an admin
        if (!currentUser.getRoles().contains("ADMIN") && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        // Update order fields
        if (orderDTO.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
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

        // Update order items (more complex logic would be needed for a real application)

        order.recalculateTotal();

        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only admins can update certain statuses
        if (!currentUser.getRoles().contains("ADMIN") &&
                (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED || status == OrderStatus.REFUNDED)) {
            throw new AccessDeniedException("You don't have permission to update this order status");
        }

        // Regular users can only cancel their own pending orders
        if (!currentUser.getRoles().contains("ADMIN") &&
                !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        // Regular users can only cancel pending orders
        if (!currentUser.getRoles().contains("ADMIN") &&
                status == OrderStatus.CANCELLED &&
                order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("You can only cancel pending orders");
        }

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }

        orderRepository.deleteById(id);
    }
}