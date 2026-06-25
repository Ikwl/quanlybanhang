package com.example.quanlybanhang.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record LoginRequest(String username, String password) {
    }

    public record CreateCustomerRequest(String username, String password, String address, String phoneNumber, String email) {
    }

    public record CreateInventoryRequest(String name, String address, String description) {
    }

    public record CreateProductRequest(Long inventoryId, String productName, String info, BigDecimal price, String pictureUrl, String category, Integer quantity, String status) {
    }

    public record UpdateQuantityRequest(Integer quantity) {
    }

    public record AddCartItemRequest(Long productId, Integer quantity) {
    }

    public record UpdateCartItemRequest(Integer quantity) {
    }

    public record OrderLineRequest(Long productId, Integer quantity) {
    }

    public record CreateOrderFromCartRequest(Long customerId, String address, String shippingType, BigDecimal shippingCost, String paymentMethod) {
    }

    public record CreateOrderRequest(Long customerId, String address, String shippingType, BigDecimal shippingCost, String paymentMethod, List<OrderLineRequest> items) {
    }

    public record UpdateOrderStatusRequest(String status) {
    }

    public record CancelOrderRequest(String cancelReason) {
    }

    public record PayOrderRequest(String method) {
    }

    public record UpdatePaymentStatusRequest(String status) {
    }
}