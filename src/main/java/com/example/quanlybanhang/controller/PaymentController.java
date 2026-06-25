package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> pay(@PathVariable Long orderId, @RequestBody ApiDtos.PayOrderRequest request) {
        return ResponseEntity.ok(paymentService.payOrder(orderId, request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId));
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStatus(@PathVariable Long paymentId, @RequestBody ApiDtos.UpdatePaymentStatusRequest request) {
        return ResponseEntity.ok(paymentService.updateStatus(paymentId, request));
    }
}