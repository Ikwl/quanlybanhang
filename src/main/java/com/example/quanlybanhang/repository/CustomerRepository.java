package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Customer findByEmail(String email);

	Customer findByUser_UserId(Long userId);
}
