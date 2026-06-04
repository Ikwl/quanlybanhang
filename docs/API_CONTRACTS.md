# API_CONTRACTS — Shop Demo

Tài liệu này tập hợp các endpoint chính của hệ thống **Quản lý bán hàng trực tuyến**.

**Base URL:** `/api`  
**Auth:** Demo local, có thể truyền `customerId` / `managerId` trong request khi chưa làm JWT.  
**Response envelope:** `{ "success": bool, "message": "...", "data": {...} }`

---

## 1. Auth / User

### POST /auth/login

- **Actor:** Guest
- **DB:** `User`, `Customer`, `Manager`
- **Mục đích:** Đăng nhập tài khoản khách hàng hoặc quản lý.
- **Body:**

```json
{
  "username": "customer01",
  "password": "123456"
}
```

- **Response 200:**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "username": "customer01",
    "role": "CUSTOMER",
    "customerId": 1,
    "managerId": null
  }
}
```

### POST /customers

- **Actor:** Guest
- **DB:** `User`, `Customer`, `Cart`
- **Mục đích:** Tạo tài khoản khách hàng mới.
- **Body:**

```json
{
  "username": "customer01",
  "password": "123456",
  "address": "Ha Noi",
  "phoneNumber": "0987654321",
  "email": "customer@gmail.com"
}
```

- **Response 201:** created customer data

### GET /customers/:customerId

- **Actor:** Customer / Manager
- **DB:** `Customer`
- **Mục đích:** Xem thông tin khách hàng.
- **Response 200:** customer data

---

## 2. Inventory

### GET /inventories

- **Actor:** Guest+
- **DB:** `Inventory`
- **Mục đích:** Lấy danh sách kho hàng.
- **Response 200:**

```json
{
  "success": true,
  "message": "Inventories retrieved successfully",
  "data": [
    {
      "inventoryId": 1,
      "name": "Kho Ha Noi",
      "address": "Cau Giay, Ha Noi",
      "description": "Kho chinh"
    }
  ]
}
```

### GET /inventories/:inventoryId

- **Actor:** Guest+
- **DB:** `Inventory`
- **Mục đích:** Xem chi tiết một kho hàng.
- **Response 200:** inventory data

### POST /inventories

- **Actor:** Manager
- **DB:** `Inventory`
- **Mục đích:** Tạo kho hàng mới.
- **Body:**

```json
{
  "name": "Kho Ha Noi",
  "address": "Cau Giay, Ha Noi",
  "description": "Kho chinh"
}
```

- **Response 201:** created inventory data

### PUT /inventories/:inventoryId

- **Actor:** Manager
- **DB:** `Inventory`
- **Mục đích:** Cập nhật thông tin kho hàng.
- **Body:** same as POST /inventories
- **Response 200:** updated inventory data

### DELETE /inventories/:inventoryId

- **Actor:** Manager
- **DB:** `Inventory`
- **Mục đích:** Xóa kho hàng.
- **Response 200:** `{ "success": true, "message": "Inventory deleted successfully", "data": null }`

---

## 3. Product

### GET /products

- **Actor:** Guest+
- **DB:** `Product`, `Inventory`
- **Mục đích:** Lấy danh sách sản phẩm.
- **Query params:** `inventoryId`, `keyword`, `category`, `status`
- **Response 200:**

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "productId": 1,
      "inventoryId": 1,
      "productName": "Ao thun nam",
      "info": "Ao cotton",
      "price": 150000,
      "pictureUrl": "https://example.com/aothun.jpg",
      "category": "Fashion",
      "quantity": 50,
      "status": "ACTIVE"
    }
  ]
}
```

### GET /products/:productId

- **Actor:** Guest+
- **DB:** `Product`
- **Mục đích:** Xem chi tiết sản phẩm.
- **Response 200:** product data

### POST /products

- **Actor:** Manager
- **DB:** `Product`, `Inventory`
- **Mục đích:** Thêm sản phẩm vào kho.
- **Body:**

```json
{
  "inventoryId": 1,
  "productName": "Ao thun nam",
  "info": "Ao cotton",
  "price": 150000,
  "pictureUrl": "https://example.com/aothun.jpg",
  "category": "Fashion",
  "quantity": 50,
  "status": "ACTIVE"
}
```

- **Response 201:** created product data

### PUT /products/:productId

- **Actor:** Manager
- **DB:** `Product`
- **Mục đích:** Cập nhật thông tin sản phẩm.
- **Body:** same as POST /products
- **Response 200:** updated product data

### PATCH /products/:productId/quantity

- **Actor:** Manager
- **DB:** `Product`
- **Mục đích:** Cập nhật số lượng sản phẩm trong kho.
- **Body:**

```json
{
  "quantity": 45
}
```

- **Response 200:** updated product data

### DELETE /products/:productId

- **Actor:** Manager
- **DB:** `Product`
- **Mục đích:** Xóa hoặc ẩn sản phẩm.
- **Response 200:** `{ "success": true, "message": "Product deleted successfully", "data": null }`

---

## 4. Cart

### GET /customers/:customerId/cart

- **Actor:** Customer
- **DB:** `Cart`, `CartItem`, `Product`
- **Mục đích:** Xem giỏ hàng của khách hàng.
- **Response 200:**

```json
{
  "success": true,
  "message": "Cart retrieved successfully",
  "data": {
    "cartId": 1,
    "customerId": 1,
    "status": "ACTIVE",
    "items": [
      {
        "cartItemId": 1,
        "productId": 1,
        "productName": "Ao thun nam",
        "quantity": 2,
        "unitPrice": 150000,
        "totalPrice": 300000
      }
    ],
    "totalAmount": 300000
  }
}
```

### POST /customers/:customerId/cart/items

- **Actor:** Customer
- **DB:** `Cart`, `CartItem`, `Product`
- **Mục đích:** Thêm sản phẩm vào giỏ hàng.
- **Body:**

```json
{
  "productId": 1,
  "quantity": 2
}
```

- **Response 201:** updated cart data

### PUT /customers/:customerId/cart/items/:cartItemId

- **Actor:** Customer
- **DB:** `CartItem`
- **Mục đích:** Cập nhật số lượng sản phẩm trong giỏ.
- **Body:**

```json
{
  "quantity": 3
}
```

- **Response 200:** updated cart data

### DELETE /customers/:customerId/cart/items/:cartItemId

- **Actor:** Customer
- **DB:** `CartItem`
- **Mục đích:** Xóa một sản phẩm khỏi giỏ.
- **Response 200:** updated cart data

### DELETE /customers/:customerId/cart

- **Actor:** Customer
- **DB:** `CartItem`
- **Mục đích:** Xóa toàn bộ sản phẩm trong giỏ.
- **Response 200:** empty cart data

---

## 5. Order

### POST /orders/from-cart

- **Actor:** Customer
- **DB:** `Order`, `OrderItem`, `Cart`, `CartItem`, `Product`, `Payment`
- **Mục đích:** Tạo đơn hàng từ giỏ hàng.
- **Body:**

```json
{
  "customerId": 1,
  "address": "Ha Noi",
  "shippingType": "STANDARD",
  "shippingCost": 30000,
  "paymentMethod": "COD"
}
```

- **Side effect:** tạo `Order`, tạo `OrderItem`, tạo `Payment`, trừ `Product.quantity`, đổi `Cart.status`.
- **Response 201:**

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": 1,
    "customerId": 1,
    "orderDate": "2026-01-01T10:00:00",
    "address": "Ha Noi",
    "status": "PENDING",
    "shippingType": "STANDARD",
    "shippingCost": 30000,
    "totalCost": 330000,
    "items": [
      {
        "orderItemId": 1,
        "productId": 1,
        "productName": "Ao thun nam",
        "quantity": 2,
        "unitPrice": 150000,
        "totalPrice": 300000
      }
    ],
    "payment": {
      "paymentId": 1,
      "method": "COD",
      "status": "UNPAID",
      "totalPayment": 330000
    }
  }
}
```

### POST /orders

- **Actor:** Customer
- **DB:** `Order`, `OrderItem`, `Product`, `Payment`
- **Mục đích:** Tạo đơn hàng trực tiếp không qua giỏ hàng.
- **Body:**

```json
{
  "customerId": 1,
  "address": "Ha Noi",
  "shippingType": "STANDARD",
  "shippingCost": 30000,
  "paymentMethod": "COD",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

- **Response 201:** created order data

### GET /orders

- **Actor:** Manager
- **DB:** `Order`, `Customer`, `Payment`
- **Mục đích:** Lấy danh sách đơn hàng.
- **Query params:** `customerId`, `status`, `page`, `limit`
- **Response 200:** list of orders

### GET /orders/:orderId

- **Actor:** Customer / Manager
- **DB:** `Order`, `OrderItem`, `Payment`
- **Mục đích:** Xem chi tiết đơn hàng.
- **Response 200:** order detail

### GET /customers/:customerId/orders

- **Actor:** Customer
- **DB:** `Order`
- **Mục đích:** Xem danh sách đơn hàng của khách hàng.
- **Response 200:** list of customer orders

### PATCH /orders/:orderId/status

- **Actor:** Manager
- **DB:** `Order`
- **Mục đích:** Cập nhật trạng thái xử lý / giao hàng của đơn hàng.
- **Body:**

```json
{
  "status": "CONFIRMED"
}
```

- **Note:** status ∈ `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPING`, `DELIVERED`, `CANCELLED`
- **Response 200:** updated order data

### PATCH /orders/:orderId/cancel

- **Actor:** Customer / Manager
- **DB:** `Order`, `Product`
- **Mục đích:** Hủy đơn hàng.
- **Body:**

```json
{
  "cancelReason": "Khach hang yeu cau huy"
}
```

- **Side effect:** set `Order.status = CANCELLED`; nếu cần thì hoàn lại `Product.quantity`.
- **Response 200:** updated order data

---

## 6. Payment

### POST /payments/pay/:orderId

- **Actor:** Customer
- **DB:** `Payment`, `Order`
- **Mục đích:** Thanh toán đơn hàng.
- **Body:**

```json
{
  "method": "BANKING"
}
```

- **Side effect:** set `Payment.status = PAID`, set `Order.status = CONFIRMED` nếu thanh toán thành công.
- **Response 200:**

```json
{
  "success": true,
  "message": "Payment successful",
  "data": {
    "paymentId": 1,
    "orderId": 1,
    "totalPayment": 330000,
    "method": "BANKING",
    "status": "PAID",
    "paymentDate": "2026-01-01T10:10:00"
  }
}
```

### GET /payments/order/:orderId

- **Actor:** Customer / Manager
- **DB:** `Payment`
- **Mục đích:** Xem thông tin thanh toán của đơn hàng.
- **Response 200:** payment data

### PATCH /payments/:paymentId/status

- **Actor:** Manager
- **DB:** `Payment`
- **Mục đích:** Cập nhật trạng thái thanh toán thủ công.
- **Body:**

```json
{
  "status": "PAID"
}
```

- **Response 200:** updated payment data

---

## 7. Tracking

### GET /orders/:orderId/tracking

- **Actor:** Customer / Manager
- **DB:** `Order`, `Payment`
- **Mục đích:** Theo dõi trạng thái đơn hàng.
- **Response 200:**

```json
{
  "success": true,
  "message": "Order tracking retrieved successfully",
  "data": {
    "orderId": 1,
    "status": "SHIPPING",
    "paymentStatus": "PAID",
    "shippingType": "STANDARD",
    "address": "Ha Noi",
    "currentStatusText": "Don hang dang duoc giao"
  }
}
```

---

## 8. Enum dùng chung

### User role

```text
CUSTOMER
MANAGER
```

### Product status

```text
ACTIVE
INACTIVE
```

### Cart status

```text
ACTIVE
ORDERED
CANCELLED
```

### Order status

```text
PENDING
CONFIRMED
PROCESSING
SHIPPING
DELIVERED
CANCELLED
```

### Payment method

```text
COD
BANKING
E_WALLET
QR
```

### Payment status

```text
UNPAID
PAID
FAILED
CANCELLED
```

---

## Error Responses

### 400 Bad Request

```json
{
  "success": false,
  "message": "Invalid request data",
  "data": null
}
```

### 401 Unauthorized

```json
{
  "success": false,
  "message": "Unauthorized",
  "data": null
}
```

### 403 Forbidden

```json
{
  "success": false,
  "message": "You do not have permission to perform this action",
  "data": null
}
```

### 404 Not Found

```json
{
  "success": false,
  "message": "Resource not found",
  "data": null
}
```

### 409 Conflict

```json
{
  "success": false,
  "message": "Product quantity is not enough",
  "data": null
}
```

---

## General Notes

- **Pagination:** Endpoint dạng danh sách có thể dùng `page` và `limit`.
- **Date Format:** `yyyy-MM-dd'T'HH:mm:ss`
- **Currency:** VND, lưu bằng `DECIMAL(12,2)` hoặc số nguyên tùy backend thống nhất.
- **Demo Payment:** Thanh toán có thể giả lập, chưa cần tích hợp cổng thanh toán thật.
- **Order Rule:** Khi tạo đơn, backend cần kiểm tra `Product.quantity` trước khi trừ tồn kho.
- **Delete Rule:** Với `Product`, nên ưu tiên đổi `status = INACTIVE` thay vì xóa cứng nếu sản phẩm đã nằm trong đơn hàng.
