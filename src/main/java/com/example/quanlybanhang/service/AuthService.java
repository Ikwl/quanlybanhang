package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Cart;
import com.example.quanlybanhang.model.Customer;
import com.example.quanlybanhang.model.User;
import com.example.quanlybanhang.model.UserRole;
import com.example.quanlybanhang.repository.CartRepository;
import com.example.quanlybanhang.repository.CustomerRepository;
import com.example.quanlybanhang.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    public AuthService(UserRepository userRepository, CustomerRepository customerRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> login(ApiDtos.LoginRequest request) {
        if (request == null || request.username() == null || request.username().isBlank() || request.password() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        User user = userRepository.findByUsername(request.username());
        if (user == null || !request.password().equals(user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return ApiResponse.success("Login successful", ResponseMapper.login(user));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> registerCustomer(ApiDtos.CreateCustomerRequest request) {
        if (request == null || request.username() == null || request.username().isBlank() || request.password() == null || request.password().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        if (userRepository.findByUsername(request.username()) != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (request.email() != null && customerRepository.findByEmail(request.email()) != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = userRepository.save(new User(request.username(), request.password(), UserRole.CUSTOMER));
        Customer customer = new Customer(user);
        customer.setAddress(request.address());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setEmail(request.email());
        customer = customerRepository.save(customer);

        Cart cart = new Cart(customer);
        cartRepository.save(cart);

        return ApiResponse.success("Customer created successfully", ResponseMapper.customer(customer));
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        return ApiResponse.success("Customer retrieved successfully", ResponseMapper.customer(customer));
    }
}
