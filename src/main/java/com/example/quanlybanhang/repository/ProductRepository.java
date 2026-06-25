package com.example.quanlybanhang.repository;

import com.example.quanlybanhang.model.Product;
import com.example.quanlybanhang.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    List<Product> findByInventory_InventoryId(Long inventoryId);

    List<Product> findByCategory(String category);

    List<Product> findByStatus(ProductStatus status);
}