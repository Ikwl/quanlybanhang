package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Cart;
import com.example.quanlybanhang.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByCustomer_CustomerId(Long customerId);

	java.util.List<Cart> findByStatus(CartStatus status);
}
