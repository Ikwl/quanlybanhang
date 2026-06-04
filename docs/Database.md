# Database Design - Shop Demo

## 1. Danh sách bảng và mô tả

| STT | Bảng | Mô tả |
|---:|---|---|
| 1 | `User` | Lưu thông tin tài khoản đăng nhập. |
| 2 | `Customer` | Lưu thông tin khách hàng. |
| 3 | `Manager` | Lưu thông tin quản lý. |
| 4 | `Inventory` | Lưu thông tin kho hàng. |
| 5 | `Product` | Lưu thông tin sản phẩm thuộc kho tương ứng. |
| 6 | `Cart` | Lưu giỏ hàng của khách hàng. |
| 7 | `CartItem` | Lưu sản phẩm trong giỏ hàng. |
| 8 | `Order` | Lưu thông tin đơn hàng. |
| 9 | `OrderItem` | Lưu chi tiết sản phẩm trong đơn hàng. |
| 10 | `Payment` | Lưu thông tin thanh toán của đơn hàng. |

---

## 2. Chi tiết từng bảng

## 2.1. `User`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `userId` | BIGINT | PK, AUTO_INCREMENT |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE |
| `password` | VARCHAR(255) | NOT NULL |
| `role` | ENUM('CUSTOMER', 'MANAGER') | NOT NULL |
| `createdAt` | DATETIME | DEFAULT CURRENT_TIMESTAMP |

---

## 2.2. `Customer`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `customerId` | BIGINT | PK, AUTO_INCREMENT |
| `userId` | BIGINT | FK → `User(userId)`, NOT NULL, UNIQUE |
| `address` | VARCHAR(255) |  |
| `phoneNumber` | VARCHAR(20) |  |
| `email` | VARCHAR(100) | UNIQUE |

---

## 2.3. `Manager`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `managerId` | BIGINT | PK, AUTO_INCREMENT |
| `userId` | BIGINT | FK → `User(userId)`, NOT NULL, UNIQUE |
| `role` | VARCHAR(50) | Ví dụ: ADMIN, STAFF |

---

## 2.4. `Inventory`

Lưu thông tin kho hàng.  
Một kho có thể chứa nhiều sản phẩm.

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `inventoryId` | BIGINT | PK, AUTO_INCREMENT |
| `name` | VARCHAR(150) | NOT NULL |
| `address` | VARCHAR(255) | Địa chỉ kho |
| `description` | VARCHAR(255) | Mô tả thêm về kho |
| `createdAt` | DATETIME | DEFAULT CURRENT_TIMESTAMP |
| `updatedAt` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

---

## 2.5. `Product`

Lưu thông tin sản phẩm và số lượng sản phẩm tại kho tương ứng.

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `productId` | BIGINT | PK, AUTO_INCREMENT |
| `inventoryId` | BIGINT | FK → `Inventory(inventoryId)`, NOT NULL |
| `productName` | VARCHAR(150) | NOT NULL |
| `info` | TEXT |  |
| `price` | DECIMAL(12,2) | NOT NULL |
| `pictureUrl` | VARCHAR(255) |  |
| `category` | VARCHAR(100) |  |
| `quantity` | INT | NOT NULL, DEFAULT 0 |
| `status` | ENUM('ACTIVE', 'INACTIVE') | DEFAULT 'ACTIVE' |

---

## 2.6. `Cart`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `cartId` | BIGINT | PK, AUTO_INCREMENT |
| `customerId` | BIGINT | FK → `Customer(customerId)`, NOT NULL, UNIQUE |
| `status` | ENUM('ACTIVE', 'ORDERED', 'CANCELLED') | DEFAULT 'ACTIVE' |
| `createdAt` | DATETIME | DEFAULT CURRENT_TIMESTAMP |
| `updatedAt` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

---

## 2.7. `CartItem`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `cartItemId` | BIGINT | PK, AUTO_INCREMENT |
| `cartId` | BIGINT | FK → `Cart(cartId)`, NOT NULL |
| `productId` | BIGINT | FK → `Product(productId)`, NOT NULL |
| `quantity` | INT | NOT NULL |
| `unitPrice` | DECIMAL(12,2) | NOT NULL |
| `totalPrice` | DECIMAL(12,2) | NOT NULL |

Ràng buộc nên có:

| Ràng buộc | Ghi chú |
|---|---|
| `UNIQUE(cartId, productId)` | Không cho một sản phẩm xuất hiện nhiều dòng trong cùng giỏ hàng. |

---

## 2.8. `Order`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `orderId` | BIGINT | PK, AUTO_INCREMENT |
| `customerId` | BIGINT | FK → `Customer(customerId)`, NOT NULL |
| `orderDate` | DATETIME | DEFAULT CURRENT_TIMESTAMP |
| `address` | VARCHAR(255) | Địa chỉ nhận hàng |
| `status` | ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED') | DEFAULT 'PENDING' |
| `shippingType` | VARCHAR(50) | Ví dụ: STANDARD |
| `shippingCost` | DECIMAL(12,2) | DEFAULT 0 |
| `totalCost` | DECIMAL(12,2) | NOT NULL |
| `cancelReason` | VARCHAR(255) |  |

---

## 2.9. `OrderItem`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `orderItemId` | BIGINT | PK, AUTO_INCREMENT |
| `orderId` | BIGINT | FK → `Order(orderId)`, NOT NULL |
| `productId` | BIGINT | FK → `Product(productId)`, NOT NULL |
| `productName` | VARCHAR(150) | Lưu tên sản phẩm tại thời điểm đặt hàng |
| `quantity` | INT | NOT NULL |
| `unitPrice` | DECIMAL(12,2) | NOT NULL |
| `totalPrice` | DECIMAL(12,2) | NOT NULL |

---

## 2.10. `Payment`

| Cột | Kiểu dữ liệu | Ghi chú |
|---|---|---|
| `paymentId` | BIGINT | PK, AUTO_INCREMENT |
| `orderId` | BIGINT | FK → `Order(orderId)`, NOT NULL, UNIQUE |
| `totalPayment` | DECIMAL(12,2) | NOT NULL |
| `method` | ENUM('COD', 'BANKING', 'E_WALLET', 'QR') | NOT NULL |
| `status` | ENUM('UNPAID', 'PAID', 'FAILED', 'CANCELLED') | DEFAULT 'UNPAID' |
| `paymentDate` | DATETIME |  |

---

## 3. Quan hệ giữa các bảng

| Quan hệ | Mô tả |
|---|---|
| `User` 1 - 1 `Customer` | Một tài khoản khách hàng có một hồ sơ khách hàng. |
| `User` 1 - 1 `Manager` | Một tài khoản quản lý có một hồ sơ quản lý. |
| `Inventory` 1 - n `Product` | Một kho chứa nhiều sản phẩm. |
| `Customer` 1 - 1 `Cart` | Một khách hàng có một giỏ hàng. |
| `Cart` 1 - n `CartItem` | Một giỏ hàng có nhiều sản phẩm. |
| `Product` 1 - n `CartItem` | Một sản phẩm có thể xuất hiện trong nhiều giỏ hàng. |
| `Customer` 1 - n `Order` | Một khách hàng có thể tạo nhiều đơn hàng. |
| `Order` 1 - n `OrderItem` | Một đơn hàng có nhiều sản phẩm. |
| `Product` 1 - n `OrderItem` | Một sản phẩm có thể xuất hiện trong nhiều đơn hàng. |
| `Order` 1 - 1 `Payment` | Một đơn hàng có một thông tin thanh toán. |
