package com.example.quanlybanhang.controller;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/from-cart")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createFromCart(@RequestBody ApiDtos.CreateOrderFromCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createFromCart(request));
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createDirect(@RequestBody ApiDtos.CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createDirect(request));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrders(@RequestParam(required = false) Long customerId,
                                                                            @RequestParam(required = false) String status,
                                                                            @RequestParam(required = false) Integer page,
                                                                            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(orderService.getOrders(customerId, status, page, limit));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @GetMapping("/customers/{customerId}/orders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCustomerOrders(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStatus(@PathVariable Long orderId, @RequestBody ApiDtos.UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelOrder(@PathVariable Long orderId, @RequestBody(required = false) ApiDtos.CancelOrderRequest request) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, request));
    }

    @GetMapping("/orders/{orderId}/tracking")
    public ResponseEntity<ApiResponse<Map<String, Object>>> tracking(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.tracking(orderId));
    }
}