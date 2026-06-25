package com.example.quanlybanhang.service;

import com.example.quanlybanhang.model.Cart;
import com.example.quanlybanhang.model.CartItem;
import com.example.quanlybanhang.model.Customer;
import com.example.quanlybanhang.model.Inventory;
import com.example.quanlybanhang.model.Manager;
import com.example.quanlybanhang.model.Order;
import com.example.quanlybanhang.model.OrderItem;
import com.example.quanlybanhang.model.Payment;
import com.example.quanlybanhang.model.PaymentStatus;
import com.example.quanlybanhang.model.Product;
import com.example.quanlybanhang.model.User;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static Map<String, Object> login(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        Customer customer = user.getCustomer();
        Manager manager = user.getManager();
        data.put("customerId", customer != null ? customer.getCustomerId() : null);
        data.put("managerId", manager != null ? manager.getManagerId() : null);
        return data;
    }

    public static Map<String, Object> customer(Customer customer) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customerId", customer.getCustomerId());
        data.put("userId", customer.getUser() != null ? customer.getUser().getUserId() : null);
        data.put("username", customer.getUser() != null ? customer.getUser().getUsername() : null);
        data.put("address", customer.getAddress());
        data.put("phoneNumber", customer.getPhoneNumber());
        data.put("email", customer.getEmail());
        return data;
    }

    public static Map<String, Object> inventory(Inventory inventory) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("inventoryId", inventory.getInventoryId());
        data.put("name", inventory.getName());
        data.put("address", inventory.getAddress());
        data.put("description", inventory.getDescription());
        return data;
    }

    public static Map<String, Object> product(Product product) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("productId", product.getProductId());
        data.put("inventoryId", product.getInventory() != null ? product.getInventory().getInventoryId() : null);
        data.put("productName", product.getProductName());
        data.put("info", product.getInfo());
        data.put("price", product.getPrice());
        data.put("pictureUrl", product.getPictureUrl());
        data.put("category", product.getCategory());
        data.put("quantity", product.getQuantity());
        data.put("status", product.getStatus());
        return data;
    }

    public static Map<String, Object> cartItem(CartItem item) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cartItemId", item.getCartItemId());
        data.put("productId", item.getProduct() != null ? item.getProduct().getProductId() : null);
        data.put("productName", item.getProduct() != null ? item.getProduct().getProductName() : null);
        data.put("quantity", item.getQuantity());
        data.put("unitPrice", item.getUnitPrice());
        data.put("totalPrice", item.getTotalPrice());
        return data;
    }

    public static Map<String, Object> cart(Cart cart, List<CartItem> items) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cartId", cart.getCartId());
        data.put("customerId", cart.getCustomer() != null ? cart.getCustomer().getCustomerId() : null);
        data.put("status", cart.getStatus());
        data.put("items", items.stream().map(ResponseMapper::cartItem).toList());
        data.put("totalAmount", items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return data;
    }

    public static Map<String, Object> orderItem(OrderItem item) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderItemId", item.getOrderItemId());
        data.put("productId", item.getProduct() != null ? item.getProduct().getProductId() : null);
        data.put("productName", item.getProductName());
        data.put("quantity", item.getQuantity());
        data.put("unitPrice", item.getUnitPrice());
        data.put("totalPrice", item.getTotalPrice());
        return data;
    }

    public static Map<String, Object> payment(Payment payment) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paymentId", payment.getPaymentId());
        data.put("orderId", payment.getOrder() != null ? payment.getOrder().getOrderId() : null);
        data.put("totalPayment", payment.getTotalPayment());
        data.put("method", payment.getMethod());
        data.put("status", payment.getStatus());
        data.put("paymentDate", payment.getPaymentDate());
        return data;
    }

    public static Map<String, Object> orderSummary(Order order) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("customerId", order.getCustomer() != null ? order.getCustomer().getCustomerId() : null);
        data.put("orderDate", order.getOrderDate());
        data.put("address", order.getAddress());
        data.put("status", order.getStatus());
        data.put("shippingType", order.getShippingType());
        data.put("shippingCost", order.getShippingCost());
        data.put("totalCost", order.getTotalCost());
        return data;
    }

    public static Map<String, Object> orderDetail(Order order, List<OrderItem> items, Payment payment) {
        Map<String, Object> data = orderSummary(order);
        data.put("cancelReason", order.getCancelReason());
        data.put("items", items.stream().map(ResponseMapper::orderItem).toList());
        data.put("payment", payment != null ? payment(payment) : null);
        return data;
    }

    public static Map<String, Object> tracking(Order order, Payment payment, String currentStatusText) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("status", order.getStatus());
        data.put("paymentStatus", payment != null ? payment.getStatus() : PaymentStatus.UNPAID);
        data.put("shippingType", order.getShippingType());
        data.put("address", order.getAddress());
        data.put("currentStatusText", currentStatusText);
        return data;
    }
}