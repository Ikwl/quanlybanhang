package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	List<CartItem> findByCart_CartId(Long cartId);

	List<CartItem> findByCart_Customer_CustomerId(Long customerId);

	CartItem findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);

	void deleteByCart_CartId(Long cartId);
}
