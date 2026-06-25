package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Order;
import com.example.quanlybanhang.model.OrderStatus;
import com.example.quanlybanhang.model.Payment;
import com.example.quanlybanhang.model.PaymentMethod;
import com.example.quanlybanhang.model.PaymentStatus;
import com.example.quanlybanhang.repository.OrderRepository;
import com.example.quanlybanhang.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ApiResponse<Map<String, Object>> payOrder(Long orderId, ApiDtos.PayOrderRequest request) {
        if (request == null || request.method() == null || request.method().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Payment payment = requirePayment(orderId);
        Order order = payment.getOrder();
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ApiException(HttpStatus.CONFLICT, "Order cannot be paid");
        }
        payment.setMethod(parseMethod(request.method()));
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        return ApiResponse.success("Payment successful", ResponseMapper.payment(payment));
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getPaymentByOrder(Long orderId) {
        return ApiResponse.success("Payment retrieved successfully", ResponseMapper.payment(requirePayment(orderId)));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateStatus(Long paymentId, ApiDtos.UpdatePaymentStatusRequest request) {
        if (request == null || request.status() == null || request.status().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        PaymentStatus status = parseStatus(request.status());
        payment.setStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaymentDate(LocalDateTime.now());
            Order order = payment.getOrder();
            if (order != null) {
                order.setStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
        }
        return ApiResponse.success("Payment updated successfully", ResponseMapper.payment(paymentRepository.save(payment)));
    }

    private Payment requirePayment(Long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId);
        if (payment == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return payment;
    }

    private PaymentMethod parseMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.trim().toUpperCase());
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }

    private PaymentStatus parseStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }
}
