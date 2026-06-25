package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Payment;
import com.example.quanlybanhang.model.PaymentMethod;
import com.example.quanlybanhang.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByOrder_OrderId(Long orderId);

	List<Payment> findByStatus(PaymentStatus status);

	List<Payment> findByMethod(PaymentMethod method);
}
