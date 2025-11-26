
## 🏛️ Phần 1: Tính năng Chi tiết (Features)

Chia các tính năng theo 3 vai trò: **Khách** (Chưa đăng nhập), **Người dùng** (Đã đăng nhập), và **Quản trị viên**.

### 1\. Khách (Guest / Anonymous)

* **Xem Trang chủ:** Thấy các tài liệu mới nhất, tài liệu được tải nhiều nhất.
* **Tìm kiếm Tài liệu:** Tìm kiếm theo từ khóa (tiêu đề, mô tả).
* **Lọc Tài liệu:** Lọc theo `Category` (môn học) hoặc `Tag`.
* **Xem Chi tiết Tài liệu:** Xem trang chi tiết (mô tả, lượt xem, lượt tải, điểm đánh giá, và các bình luận).
* **Đăng ký tài khoản:** Truy cập trang đăng ký.
* **Đăng nhập:** Truy cập trang đăng nhập (trang `login.html` bạn đã tạo).

### 2\. Người dùng (User / `ROLE_USER`)

* *(Bao gồm tất cả tính năng của Khách)*
* **Upload Tài liệu:** Tải file (PDF, DOCX...) lên hệ thống. Tài liệu này sẽ có trạng thái `PENDING` (chờ duyệt).
* **Download Tài liệu:** Tải file gốc của các tài liệu đã được `APPROVED`.
* **Bình luận:** Viết bình luận/trả lời bình luận trong trang chi tiết tài liệu.
* **Đánh giá (Rating):** Cho điểm (1-5 sao) cho tài liệu.
* **Quản lý Hồ sơ:**
    * Xem thông tin cá nhân.
    * Đổi mật khẩu.
* **Quản lý Tài liệu Cá nhân:**
    * Xem danh sách tài liệu mình đã upload (và trạng thái của chúng).
    * (Tùy chọn) Chỉnh sửa/Xóa tài liệu của mình *nếu* nó chưa được duyệt.

### 3\. Quản trị viên (Admin / `ROLE_ADMIN`)

* *(Bao gồm tất cả tính năng của Người dùng)*
* **Truy cập Trang Admin:** Có một khu vực quản trị riêng (ví dụ: `/admin`).
* **Quản lý Phê duyệt (Moderation):**
    * Xem danh sách tài liệu đang `PENDING`.
    * **Phê duyệt (Approve):** Chuyển trạng thái tài liệu sang `APPROVED` (hiển thị công khai).
    * **Từ chối (Reject):** Chuyển trạng thái sang `REJECTED`.
* **Quản lý Toàn bộ Tài liệu:**
    * Chỉnh sửa hoặc Xóa **bất kỳ** tài liệu nào.
* **Quản lý Người dùng:**
    * Xem danh sách tất cả người dùng.
    * Vô hiệu hóa (Disable) hoặc Kích hoạt (Enable) tài khoản người dùng.
    * Phân quyền (ví dụ: nâng cấp User thành Admin).
* **Quản lý Phân loại (Taxonomy):**
    * Tạo / Sửa / Xóa `Categories` (Môn học).
    * Tạo / Sửa / Xóa `Tags`.
* **Quản lý Bình luận:** Xóa **bất kỳ** bình luận nào (ví dụ: spam).

-----

## 🌳 Phần 2: Cấu trúc Thư mục và Chức năng File

Đây là cấu trúc `src/main/java/com/tailieuptit/demo` đầy đủ và chức năng của từng file quan trọng.

```
demo
│
├── DemoApplication.java       // File chạy chính Spring Boot
│
├── config                     // Gói Cấu hình (Security, Web)
│   ├── SecurityConfig.java    // **Rất quan trọng**: Cấu hình Spring Security (phân quyền, mã hóa pass)
│   └── WebConfig.java         // (Tùy chọn) Cấu hình CORS, Resource Handlers
│
├── controller                 // Gói Controller (API Endpoints)
│   ├── AuthController.java      // Xử lý Đăng ký, Đăng nhập (trả về trang HTML, xử lý API)
│   ├── DocumentController.java  // Xử lý API công khai (lấy DS, chi tiết, tìm kiếm)
│   ├── UserActionController.java // Xử lý API cần đăng nhập (upload, download, comment, rate)
│   └── AdminController.java     // Xử lý API cho Admin (duyệt, xóa user, ...)
│
├── dto                        // Gói DTO (Data Transfer Objects - "Hộp" chứa dữ liệu)
│   ├── RegisterRequest.java   // DTO cho form đăng ký (username, password, email)
│   ├── DocumentSummaryDTO.java // DTO tóm tắt (hiển thị ở danh sách)
│   ├── DocumentDetailDTO.java  // DTO chi tiết (hiển thị ở trang chi tiết)
│   ├── CommentDTO.java        // DTO cho bình luận (bao gồm tên user, nội dung, replies)
│   └── MessageResponse.java   // DTO trả về thông báo chung ({"message": "Thành công!"})
│
├── entity                     // Gói Entity (Ánh xạ CSDL)
│   ├── User.java              // (Implement UserDetails)
│   ├── Role.java
│   ├── Document.java
│   ├── Category.java
│   ├── Tag.java
│   ├── Comment.java
│   ├── Rating.java
│   └── DocumentStatus.java    // (Enum: PENDING, APPROVED, REJECTED)
│
├── repository                 // Gói Repository (Truy vấn CSDL)
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── DocumentRepository.java
│   ├── CategoryRepository.java
│   ├── TagRepository.java
│   ├── CommentRepository.java
│   └── RatingRepository.java
│
├── service                    // Gói Service (Logic nghiệp vụ)
│   ├── impl                   // (Chứa các file implementation)
│   │   ├── UserServiceImpl.java
│   │   ├── DocumentServiceImpl.java
│   │   ├── FileStorageServiceImpl.java // Logic lưu file vào thư mục /uploads
│   │   └── InteractionServiceImpl.java // Logic xử lý (comment, rating)
│   │
│   ├── UserService.java       // (Interface - Implement UserDetailsService)
│   ├── DocumentService.java   // (Interface)
│   ├── FileStorageService.java // (Interface - Tách biệt logic lưu file)
│   └── InteractionService.java // (Interface)
│
├── exception                  // Gói Xử lý Ngoại lệ
│   ├── ResourceNotFoundException.java // Lỗi tùy chỉnh khi không tìm thấy (VD: Document 404)
│   ├── FileUploadException.java       // Lỗi tùy chỉnh khi upload file
│   └── GlobalExceptionHandler.java    // (@RestControllerAdvice) Bắt lỗi toàn cục và trả về JSON
│
└── util                       // Gói Tiện ích
    ├── SecurityUtil.java      // (Tùy chọn) Hàm tiện ích lấy User đang đăng nhập
    └── SlugUtil.java          // (Tùy chọn) Hàm tạo slug ('Lap Trinh' -> 'lap-trinh')
```

-----

## 📡 Phần 3: API Endpoints và Chức năng

Đây là các API endpoints (đường dẫn) mà `Controller` sẽ cung cấp.

### 1\. AuthController (Xác thực)

* **`GET /auth/login`**: (Public) Trả về trang `login.html` (Thymeleaf).
* **`GET /auth/register`**: (Public) Trả về trang `register.html` (Thymeleaf).
* **`POST /api/auth/register`**: (Public)
    * **Chức năng:** Nhận `RegisterRequest` (JSON), gọi `UserService.registerUser()` để tạo tài khoản mới.

### 2\. DocumentController (API Công khai)

* **`GET /api/documents`**: (Public)
    * **Chức năng:** Lấy danh sách tài liệu đã `APPROVED` (có phân trang `?page=0&size=10`).
* **`GET /api/documents/search`**: (Public)
    * **Chức năng:** Tìm kiếm tài liệu (ví dụ: `?keyword=java&categoryId=5`).
* **`GET /api/documents/{id}`**: (Public)
    * **Chức năng:** Lấy chi tiết tài liệu (phải là `APPROVED`). Gọi `DocumentService.getDocumentDetails(id)`.
* **`GET /api/categories`**: (Public)
    * **Chức năng:** Lấy danh sách tất cả `Categories` (để hiển thị menu lọc).

### 3\. UserActionController (API Cần đăng nhập)

* **`POST /api/documents/upload`**: (Yêu cầu `ROLE_USER`)
    * **Chức năng:** Nhận `MultipartFile` và metadata (title, categoryId). Gọi `DocumentService.uploadDocument()` để lưu file (với status `PENDING`).
* **`GET /api/documents/download/{id}`**: (Yêu cầu `ROLE_USER`)
    * **Chức năng:** Gọi `DocumentService.downloadDocument(id)`, trả về file để tải xuống và tăng `download_count`.
* **`POST /api/documents/{id}/comment`**: (Yêu cầu `ROLE_USER`)
    * **Chức năng:** Gửi bình luận mới. Gọi `InteractionService.addComment()`.
* **`POST /api/documents/{id}/rate`**: (Yêu cầu `ROLE_USER`)
    * **Chức năng:** Gửi đánh giá (score 1-5). Gọi `InteractionService.addRating()`.
* **`DELETE /api/comments/{id}`**: (Yêu cầu `ROLE_USER` / `ROLE_ADMIN`)
    * **Chức năng:** Xóa bình luận (chỉ cho phép chủ sở hữu hoặc Admin).
* **`GET /api/users/me`**: (Yêu cầu `ROLE_USER`)
    * **Chức năng:** Lấy thông tin chi tiết của người dùng đang đăng nhập.

### 4\. AdminController (API Quản trị)

*Tất cả các API này đều yêu cầu `ROLE_ADMIN` (được cấu hình trong `SecurityConfig` cho đường dẫn `/api/admin/**`)*

* **`GET /api/admin/documents/pending`**:
    * **Chức năng:** Lấy danh sách tài liệu đang chờ duyệt (status `PENDING`).
* **`POST /api/admin/documents/{id}/approve`**:
    * **Chức năng:** Chuyển status tài liệu sang `APPROVED`.
* **`POST /api/admin/documents/{id}/reject`**:
    * **Chức năng:** Chuyển status tài liệu sang `REJECTED`.
* **`DELETE /api/admin/documents/{id}`**:
    * **Chức năng:** Xóa vĩnh viễn một tài liệu.
* **`GET /api/admin/users`**:
    * **Chức năng:** Lấy danh sách tất cả người dùng (phân trang).
* **`DELETE /api/admin/users/{id}`**:
    * **Chức năng:** Vô hiệu hóa/Xóa một người dùng.