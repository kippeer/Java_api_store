package com.store.repository;

import com.store.entity.Order;
import com.store.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN o.items i " +
            "LEFT JOIN i.product p " +
            "WHERE (:userId IS NULL OR o.user.id = :userId) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:productId IS NULL OR p.id = :productId) " +
            "AND (:minQuantity IS NULL OR i.quantity >= :minQuantity) " +
            "AND (:maxQuantity IS NULL OR i.quantity <= :maxQuantity) " +
            "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR i.price <= :maxPrice) " +
            "AND (:minAmount IS NULL OR o.totalAmount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR o.totalAmount <= :maxAmount) " +
            "AND (:startDate IS NULL OR :endDate IS NULL OR o.createdAt BETWEEN :startDate AND :endDate)")
    Page<Order> findByFiltroDinamico(
            @Param("userId") Long userId,
            @Param("status") Order.OrderStatus status,
            @Param("productId") Long productId,
            @Param("minQuantity") Integer minQuantity,
            @Param("maxQuantity") Integer maxQuantity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<Order> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, Order.OrderStatus status);
}