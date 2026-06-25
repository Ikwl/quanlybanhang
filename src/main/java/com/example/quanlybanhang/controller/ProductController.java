package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProducts(@RequestParam(required = false) Long inventoryId,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(required = false) String category,
                                                                              @RequestParam(required = false) String status) {
        return ResponseEntity.ok(productService.getAllProducts(inventoryId, keyword, category, status));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProduct(@RequestBody ApiDtos.CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProduct(@PathVariable Long productId, @RequestBody ApiDtos.CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @PatchMapping("/{productId}/quantity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateQuantity(@PathVariable Long productId, @RequestBody ApiDtos.UpdateQuantityRequest request) {
        return ResponseEntity.ok(productService.updateQuantity(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }
}