package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Inventory;
import com.example.quanlybanhang.model.Product;
import com.example.quanlybanhang.model.ProductStatus;
import com.example.quanlybanhang.repository.InventoryRepository;
import com.example.quanlybanhang.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getAllProducts(Long inventoryId, String keyword, String category, String status) {
        ProductStatus parsedStatus = parseStatus(status);
        List<Map<String, Object>> data = productRepository.findAll().stream()
                .filter(product -> inventoryId == null || (product.getInventory() != null && Objects.equals(product.getInventory().getInventoryId(), inventoryId)))
                .filter(product -> keyword == null || keyword.isBlank() || (product.getProductName() != null && product.getProductName().toLowerCase().contains(keyword.toLowerCase())))
                .filter(product -> category == null || category.isBlank() || (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)))
                .filter(product -> parsedStatus == null || product.getStatus() == parsedStatus)
                .map(ResponseMapper::product)
                .toList();
        return ApiResponse.success("Products retrieved successfully", data);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        return ApiResponse.success("Product retrieved successfully", ResponseMapper.product(product));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> createProduct(ApiDtos.CreateProductRequest request) {
        if (request == null || request.inventoryId() == null || request.productName() == null || request.productName().isBlank() || request.price() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Inventory inventory = inventoryRepository.findById(request.inventoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        Product product = new Product(inventory, request.productName(), request.price(), request.quantity() != null ? request.quantity() : 0);
        product.setProductName(request.productName());
        product.setInfo(request.info());
        product.setPrice(request.price());
        product.setPictureUrl(request.pictureUrl());
        product.setCategory(request.category());
        product.setStatus(parseStatus(request.status()) != null ? parseStatus(request.status()) : ProductStatus.ACTIVE);
        return ApiResponse.success("Product created successfully", ResponseMapper.product(productRepository.save(product)));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateProduct(Long productId, ApiDtos.CreateProductRequest request) {
        if (request == null || request.inventoryId() == null || request.productName() == null || request.productName().isBlank() || request.price() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        Inventory inventory = inventoryRepository.findById(request.inventoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        product.setInventory(inventory);
        product.setProductName(request.productName());
        product.setInfo(request.info());
        product.setPrice(request.price());
        product.setPictureUrl(request.pictureUrl());
        product.setCategory(request.category());
        product.setQuantity(request.quantity() != null ? request.quantity() : product.getQuantity());
        ProductStatus parsedStatus = parseStatus(request.status());
        if (parsedStatus != null) {
            product.setStatus(parsedStatus);
        }
        return ApiResponse.success("Product updated successfully", ResponseMapper.product(productRepository.save(product)));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateQuantity(Long productId, ApiDtos.UpdateQuantityRequest request) {
        if (request == null || request.quantity() == null || request.quantity() < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        product.setQuantity(request.quantity());
        return ApiResponse.success("Product quantity updated successfully", ResponseMapper.product(productRepository.save(product)));
    }

    @Transactional
    public ApiResponse<Object> deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
        return ApiResponse.success("Product deleted successfully", null);
    }

    private ProductStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ProductStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }
}
