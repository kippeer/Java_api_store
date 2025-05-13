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

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query("""
        SELECT o FROM Order o 
        WHERE (:startDate IS NULL OR o.createdAt >= :startDate)
        AND (:endDate IS NULL OR o.createdAt <= :endDate)
        AND (:status IS NULL OR o.status = :status)
        AND (:minAmount IS NULL OR o.totalAmount >= :minAmount)
        AND (:maxAmount IS NULL OR o.totalAmount <= :maxAmount)
        AND (:userId IS NULL OR o.user.id = :userId)
    """)
    Page<Order> findOrdersByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        SELECT o FROM Order o 
        WHERE o.createdAt BETWEEN :startDate AND :endDate 
        AND o.status = :status
    """)
    List<Order> findByDateRangeAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status
    );
}