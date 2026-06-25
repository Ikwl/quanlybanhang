package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Order;
import com.example.quanlybanhang.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByCustomer_CustomerId(Long customerId);

	List<Order> findByStatus(OrderStatus status);

	List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
