package com.lxl.yz.repository;

import com.lxl.yz.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    
    @Query("SELECT o FROM Order o WHERE o.orderStatus = '已支付' AND DATE(o.orderDate) = :date")
    List<Order> findPaidOrdersByDate(@Param("date") String date);
}
