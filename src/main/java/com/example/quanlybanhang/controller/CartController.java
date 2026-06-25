package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/customers/{customerId}/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addItem(@PathVariable Long customerId, @RequestBody ApiDtos.AddCartItemRequest request) {
        return ResponseEntity.status(201).body(cartService.addItem(customerId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateItem(@PathVariable Long customerId, @PathVariable Long cartItemId, @RequestBody ApiDtos.UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(customerId, cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteItem(@PathVariable Long customerId, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.deleteItem(customerId, cartItemId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> clearCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.clearCart(customerId));
    }
}