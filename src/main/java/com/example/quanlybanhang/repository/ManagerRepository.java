package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
	Manager findByUser_UserId(Long userId);
}
