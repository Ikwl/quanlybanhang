package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	java.util.List<Inventory> findByNameContainingIgnoreCase(String keyword);
}
