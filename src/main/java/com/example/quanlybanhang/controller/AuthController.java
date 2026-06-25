package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody ApiDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCustomer(@RequestBody ApiDtos.CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerCustomer(request));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(authService.getCustomer(customerId));
    }
}