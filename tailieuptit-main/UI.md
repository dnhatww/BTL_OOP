
## 🌳 Cấu trúc `templates` (Phiên bản Hoàn chỉnh)

```
src/main/resources
├── static/
│   └── css/
│       └── style.css      <-- CSS chung của bạn
│
└── templates/
    │
    ├── layouts/           <-- 1. Gói Layouts (Khung CHA)
    │   ├── default_layout.html  // (Layout cho trang public)
    │   └── admin_layout.html    // (Layout cho trang admin)
    │
    ├── fragments/         <-- 2. Gói Fragments (Mảnh ghép)
    │   ├── header.html          // (Navbar public)
    │   ├── footer.html          // (Footer public)
    │   └── admin_sidebar.html   // (Menu admin bên trái)
    │
    ├── admin/             <-- 3. Gói Admin (Trang Quản trị)
    │   ├── dashboard.html       // (Trang chủ Admin)
    │   ├── moderation.html      // (Trang duyệt tài liệu)
    │   └── user-list.html       // (Trang quản lý user)
    │
    ├── (Các trang Public)
    │   ├── index.html           // (Trang chủ)
    │   ├── login.html           // (Trang đăng nhập)
    │   ├── register.html        // (Trang đăng ký)
    │   └── document-detail.html // (Trang xem chi tiết 1 tài liệu)
    │   
    └── (Các trang User - Cần đăng nhập)
        ├── upload.html          // (Trang Upload tài liệu)
        └── profile.html         // (Trang hồ sơ cá nhân / đổi mật khẩu)
```

-----

## 📄 Chức năng Chi tiết

### 1\. Gói `layouts/` (Khung CHA)

* **`default_layout.html`**:
  * **Chức năng:** Khung sườn cho tất cả các trang Public và User.
  * **Nhiệm vụ:** Tải `style.css`, `header.html`, `footer.html` và cung cấp một `layout:fragment="content"` cho các trang con.
* **`admin_layout.html`**:
  * **Chức năng:** Khung sườn riêng cho trang Admin.
  * **Nhiệm vụ:** Tải `admin_sidebar.html` (thay vì `header.html`) và `footer.html`.

### 2\. Gói `fragments/` (Mảnh ghép)

* **`header.html`**: (Như đã code) Navbar công khai, tích hợp Spring Security (hiển thị/ẩn nút Login/Logout).
* **`footer.html`**: (Như đã code) Chân trang.
* **`admin_sidebar.html`**: Menu điều hướng bên trái cho Admin (Duyệt tài liệu, Quản lý User...).

### 3\. Gói `admin/` (Trang Admin)

* **`dashboard.html`**: (Kế thừa `admin_layout`) Trang chủ của Admin, hiển thị thống kê.
* **`moderation.html`**: (Kế thừa `admin_layout`) Trang quan trọng nhất, hiển thị danh sách tài liệu `PENDING`. Gọi API `GET /api/admin/documents/pending`.
* **`user-list.html`**: (Kế thừa `admin_layout`) Hiển thị danh sách user. Gọi API `GET /api/admin/users`.

### 4\. Trang Public (Gốc)

* **`index.html`**: (Kế thừa `default_layout`) Trang chủ, hiển thị tài liệu mới nhất, categories.
* **`login.html`**: (Kế thừa `default_layout`) Form đăng nhập (trỏ đến `POST /auth/login`).
* **`register.html`**: (Kế thừa `default_layout`) Form đăng ký (gọi API `POST /api/auth/register`).
* **`document-detail.html`**: (Kế thừa `default_layout`) Trang chi tiết, hiển thị mô tả, file preview (nếu có), và khu vực bình luận. Gọi API `GET /api/documents/{id}`.
* **`search-results.html`**: (Kế thừa `default_layout`) Hiển thị kết quả khi người dùng search (từ form ở `header.html`).

### 5\. Trang User (Gốc)

* **`upload.html`**: (File bạn đã hỏi)
  * **Chức năng:** (Kế thừa `default_layout`) Cung cấp form cho phép người dùng chọn file, nhập Tiêu đề, Mô tả, chọn Category.
  * **API:** Form này sẽ gửi (submit) đến API `POST /api/documents/upload`.
* **`profile.html`**: (File bạn đã hỏi)
  * **Chức năng:** (Kế thừa `default_layout`) Hiển thị thông tin user (từ `GET /api/users/me`) và cung cấp form đổi mật khẩu.