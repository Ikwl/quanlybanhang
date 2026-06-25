package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.User;
import com.example.quanlybanhang.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	java.util.List<User> findByRole(UserRole role);
}
