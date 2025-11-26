# tailieuptit

# 🚀 Hướng dẫn Khởi động Dự án và Cấu hình Database

Tài liệu này hướng dẫn cách thiết lập cơ sở dữ liệu (Database) MySQL và cấu hình kết nối trong Spring Boot để khởi động ứng dụng.

## 1\. 📝 Khởi tạo Cấu trúc Bảng (Schema)

Toàn bộ cấu trúc bảng (Schema) và dữ liệu vai trò cơ bản (`ROLE_ADMIN`, `ROLE_USER`) đã được định nghĩa sẵn trong file `tailieu.sql`. Bạn chỉ cần thực thi file này.

### 1.1. Tạo Database

Đảm bảo bạn đã tạo một Database trống có tên là **`tailieu`** (hoặc tên đã cấu hình trong `application.properties`) trong MySQL Workbench.

### 1.2. Thực thi file `tailieu.sql`

Sử dụng **MySQL Workbench** hoặc **MySQL Command Line Client** để chạy file SQL đính kèm:

1.  **Sử dụng MySQL Command Line Client (Khuyến nghị):**
    ```bash
    # Đăng nhập vào MySQL
    mysql -u root -p
    # Chọn Database
    USE tailieu; 
    # Thực thi file SQL
    source /path/to/tailieu.sql; 
    ```
2.  **Sử dụng MySQL Workbench:**
    * Mở Workbench, kết nối, và chọn Database `tailieu`.
    * Vào menu **File** -\> **Open SQL Script...**, chọn file `tailieu.sql`.
    * Đảm bảo Database `tailieu` đang được chọn (làm **Schema mặc định**) và nhấn nút **Execute (biểu tượng tia sét)**.

> **Kiểm tra:** Các bảng như `users`, `documents`, `user_roles` và `roles` sẽ được tạo, và bảng `roles` sẽ được chèn dữ liệu mặc định.

-----

## 2\. ⚙️ Cấu hình Kết nối và Khởi động

Bạn cần sửa file `src/main/resources/application.properties` để khớp với mật khẩu MySQL của bạn.

### 2.1. Sửa file `application.properties`

Mở file và cập nhật mật khẩu trong mục **Database configuration**:

```properties
# ======================================================
# DATABASE CONFIGURATION (MySQL)
# ======================================================
# Giữ nguyên nếu bạn dùng database tên 'tailieu'
spring.datasource.url=jdbc:mysql://localhost:3306/tailieu?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true

spring.datasource.username=root

# CẬP NHẬT MẬT KHẨU CỦA BẠN
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE 
```

### 2.2. Khởi động Ứng dụng

1.  **Rebuild Project** trong IntelliJ IDEA (Menu Build -\> Rebuild Project).
2.  Chạy lớp chính `DemoApplication.java`.
3.  Truy cập: `http://localhost:8080/`

Bạn có thể tạo một tài khoản và kiểm tra chức năng đăng nhập/đăng ký.