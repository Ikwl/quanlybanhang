package com.example.quanlybanhang.service;

import com.example.quanlybanhang.dto.ApiDtos;
import com.example.quanlybanhang.dto.ApiResponse;
import com.example.quanlybanhang.exception.ApiException;
import com.example.quanlybanhang.model.Cart;
import com.example.quanlybanhang.model.CartItem;
import com.example.quanlybanhang.model.CartStatus;
import com.example.quanlybanhang.model.Customer;
import com.example.quanlybanhang.model.Order;
import com.example.quanlybanhang.model.OrderItem;
import com.example.quanlybanhang.model.OrderStatus;
import com.example.quanlybanhang.model.Payment;
import com.example.quanlybanhang.model.PaymentMethod;
import com.example.quanlybanhang.model.PaymentStatus;
import com.example.quanlybanhang.model.Product;
import com.example.quanlybanhang.repository.CartItemRepository;
import com.example.quanlybanhang.repository.CartRepository;
import com.example.quanlybanhang.repository.CustomerRepository;
import com.example.quanlybanhang.repository.OrderItemRepository;
import com.example.quanlybanhang.repository.OrderRepository;
import com.example.quanlybanhang.repository.PaymentRepository;
import com.example.quanlybanhang.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CustomerRepository customerRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ApiResponse<Map<String, Object>> createFromCart(ApiDtos.CreateOrderFromCartRequest request) {
        if (request == null || request.customerId() == null || request.paymentMethod() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Customer customer = getCustomer(request.customerId());
        Cart cart = cartRepository.findByCustomer_CustomerId(request.customerId());
        if (cart == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found");
        }
        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new ApiException(HttpStatus.CONFLICT, "Cart is empty");
        }
        return createOrderInternal(customer, request.address(), request.shippingType(), request.shippingCost(), parsePaymentMethod(request.paymentMethod()), cartItems, cart);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> createDirect(ApiDtos.CreateOrderRequest request) {
        if (request == null || request.customerId() == null || request.items() == null || request.items().isEmpty() || request.paymentMethod() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Customer customer = getCustomer(request.customerId());
        List<CartItem> items = new ArrayList<>();
        for (ApiDtos.OrderLineRequest line : request.items()) {
            Product product = requireProduct(line.productId());
            validateStock(product, line.quantity());
            BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(line.quantity()));
            items.add(new CartItem(null, product, line.quantity(), product.getPrice(), totalPrice));
        }
        return createOrderInternal(customer, request.address(), request.shippingType(), request.shippingCost(), parsePaymentMethod(request.paymentMethod()), items, null);
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getOrders(Long customerId, String status, Integer page, Integer limit) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> customerId == null || (order.getCustomer() != null && order.getCustomer().getCustomerId().equals(customerId)))
                .filter(order -> status == null || status.isBlank() || order.getStatus().name().equalsIgnoreCase(status))
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .toList();
        return ApiResponse.success("Orders retrieved successfully", paginate(orders, page, limit).stream().map(ResponseMapper::orderSummary).toList());
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getOrder(Long orderId) {
        Order order = findOrder(orderId);
        return ApiResponse.success("Order retrieved successfully", ResponseMapper.orderDetail(order, orderItemRepository.findByOrder_OrderId(orderId), paymentRepository.findByOrder_OrderId(orderId)));
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<Map<String, Object>>> getCustomerOrders(Long customerId) {
        getCustomer(customerId);
        return ApiResponse.success("Orders retrieved successfully", orderRepository.findByCustomer_CustomerId(customerId).stream().sorted(Comparator.comparing(Order::getOrderDate).reversed()).map(ResponseMapper::orderSummary).toList());
    }

    @Transactional
    public ApiResponse<Map<String, Object>> updateStatus(Long orderId, ApiDtos.UpdateOrderStatusRequest request) {
        if (request == null || request.status() == null || request.status().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        Order order = findOrder(orderId);
        order.setStatus(parseOrderStatus(request.status()));
        return ApiResponse.success("Order updated successfully", ResponseMapper.orderDetail(orderRepository.save(order), orderItemRepository.findByOrder_OrderId(orderId), paymentRepository.findByOrder_OrderId(orderId)));
    }

    @Transactional
    public ApiResponse<Map<String, Object>> cancelOrder(Long orderId, ApiDtos.CancelOrderRequest request) {
        Order order = findOrder(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new ApiException(HttpStatus.CONFLICT, "Order cannot be cancelled");
        }
        if (order.getStatus() != OrderStatus.CANCELLED) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderId(orderId);
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                product.setQuantity((product.getQuantity() == null ? 0 : product.getQuantity()) + item.getQuantity());
                productRepository.save(product);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(request != null ? request.cancelReason() : null);
        Payment payment = paymentRepository.findByOrder_OrderId(orderId);
        if (payment != null) {
            payment.setStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
        }
        return ApiResponse.success("Order updated successfully", ResponseMapper.orderDetail(orderRepository.save(order), orderItemRepository.findByOrder_OrderId(orderId), paymentRepository.findByOrder_OrderId(orderId)));
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> tracking(Long orderId) {
        Order order = findOrder(orderId);
        Payment payment = paymentRepository.findByOrder_OrderId(orderId);
        return ApiResponse.success("Order tracking retrieved successfully", ResponseMapper.tracking(order, payment, statusText(order.getStatus())));
    }

    private ApiResponse<Map<String, Object>> createOrderInternal(Customer customer, String address, String shippingType, BigDecimal shippingCost, PaymentMethod method, List<CartItem> items, Cart cart) {
        BigDecimal subtotal = items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = shippingCost == null ? BigDecimal.ZERO : shippingCost;
        Order order = new Order(customer, subtotal.add(shipping));
        order.setAddress(address);
        order.setShippingType(shippingType);
        order.setShippingCost(shipping);
        order = orderRepository.save(order);

        List<OrderItem> savedItems = new ArrayList<>();
        for (CartItem item : items) {
            Product product = requireProduct(item.getProduct().getProductId());
            validateStock(product, item.getQuantity());
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(order, product, item.getQuantity(), product.getPrice(), product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItem.setProductName(product.getProductName());
            savedItems.add(orderItemRepository.save(orderItem));
        }

        Payment payment = new Payment(order, order.getTotalCost(), method);
        payment.setStatus(PaymentStatus.UNPAID);
        payment = paymentRepository.save(payment);

        if (cart != null) {
            cart.setStatus(CartStatus.ORDERED);
            cartRepository.save(cart);
            cartItemRepository.deleteByCart_CartId(cart.getCartId());
        }

        return ApiResponse.success("Order created successfully", ResponseMapper.orderDetail(order, savedItems, payment));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
    }

    private Product requireProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Resource not found"));
    }

    private void validateStock(Product product, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
        if (product.getQuantity() == null || product.getQuantity() < quantity) {
            throw new ApiException(HttpStatus.CONFLICT, "Product quantity is not enough");
        }
    }

    private PaymentMethod parsePaymentMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.trim().toUpperCase());
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }

    private OrderStatus parseOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid request data");
        }
    }

    private List<Order> paginate(List<Order> orders, Integer page, Integer limit) {
        if (page == null || limit == null || limit <= 0) {
            return orders;
        }
        int start = Math.max(page, 0) * limit;
        if (start >= orders.size()) {
            return List.of();
        }
        return orders.subList(start, Math.min(start + limit, orders.size()));
    }

    private String statusText(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Don hang dang cho xu ly";
            case CONFIRMED -> "Don hang da duoc xac nhan";
            case PROCESSING -> "Don hang dang duoc xu ly";
            case SHIPPING -> "Don hang dang duoc giao";
            case DELIVERED -> "Don hang da giao thanh cong";
            case CANCELLED -> "Don hang da bi huy";
        };
    }
}
