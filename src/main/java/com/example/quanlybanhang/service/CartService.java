package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Cart;
import com.example.quanlybanhang.model.CartItem;
import com.example.quanlybanhang.model.CartStatus;
import com.example.quanlybanhang.model.Customer;
import com.example.quanlybanhang.model.Product;
import com.example.quanlybanhang.model.ProductStatus;
import com.example.quanlybanhang.repository.CartItemRepository;
import com.example.quanlybanhang.repository.CartRepository;
import com.example.quanlybanhang.repository.CustomerRepository;
import com.example.quanlybanhang.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getCart(Long customerId) {
        Cart cart = requireCart(customerId);
        return ApiResponse.success("Cart retrieved successfully", ResponseMapper.cart(cart, cartItemRepository.findByCart_CartId(cart.getCartId())));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> addItem(Long customerId, ApiDtos.AddCartItemRequest request) {
        if (request == null || request.productId() == null || request.quantity() == null || request.quantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Cart cart = requireOrCreateCart(customerId);
        if (cart.getStatus() != CartStatus.ACTIVE) {
            cart.setStatus(CartStatus.ACTIVE);
        }
        Product product = requireActiveProduct(request.productId());
        CartItem item = cartItemRepository.findByCart_CartIdAndProduct_ProductId(cart.getCartId(), product.getProductId());
        int newQuantity = request.quantity();
        if (item != null) {
            newQuantity += item.getQuantity();
        }
        ensureStock(product, newQuantity);
        if (item == null) {
            item = new CartItem(cart, product, newQuantity, product.getPrice(), product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            item.setQuantity(newQuantity);
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        }
        cartItemRepository.save(item);
        return getCart(customerId);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateItem(Long customerId, Long cartItemId, ApiDtos.UpdateCartItemRequest request) {
        if (request == null || request.quantity() == null || request.quantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        CartItem item = requireCartItem(customerId, cartItemId);
        ensureStock(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());
        item.setUnitPrice(item.getProduct().getPrice());
        item.setTotalPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(request.quantity())));
        cartItemRepository.save(item);
        return getCart(customerId);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> deleteItem(Long customerId, Long cartItemId) {
        CartItem item = requireCartItem(customerId, cartItemId);
        cartItemRepository.delete(item);
        return getCart(customerId);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> clearCart(Long customerId) {
        Cart cart = requireCart(customerId);
        cartItemRepository.deleteByCart_CartId(cart.getCartId());
        cart.setStatus(CartStatus.ACTIVE);
        cartRepository.save(cart);
        return ApiResponse.success("Cart cleared successfully", ResponseMapper.cart(cart, List.of()));
    }

    @Transactional(readOnly = true)
    public Cart requireCart(Long customerId) {
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        return cart;
    }

    @Transactional
    public Cart requireOrCreateCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId);
        if (cart == null) {
            cart = cartRepository.save(new Cart(customer));
        }
        return cart;
    }

    private CartItem requireCartItem(Long customerId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        if (item.getCart() == null || item.getCart().getCustomer() == null || !item.getCart().getCustomer().getCustomerId().equals(customerId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }
        return item;
    }

    private Product requireActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
        if (product.getStatus() == null || product.getStatus() != ProductStatus.ACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "Resource not available");
        }
        return product;
    }

    private void ensureStock(Product product, Integer quantity) {
        if (product.getQuantity() == null || product.getQuantity() < quantity) {
            throw new ApiException(HttpStatus.CONFLICT, "Product quantity is not enough");
        }
    }
}
