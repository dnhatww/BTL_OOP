package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * [LIÊN KẾT VỚI USERSERVICE & SPRING SECURITY]
     * Được gọi bởi:
     * 1. loadUserByUsername() - (Cần cho Spring Security)
     * 2. findByUsername() - (Lấy thông tin user)
     * 3. InteractionService / DocumentService - (Tìm người dùng đang hành động)
     */
    Optional<User> findByUsername(String username);

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * Được gọi bởi registerUser() để kiểm tra username đã tồn tại chưa.
     */
    Boolean existsByUsername(String username);

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * Được gọi bởi registerUser() để kiểm tra email đã tồn tại chưa.
     */
    Boolean existsByEmail(String email);

    // Hàm 'findByEmail' (trả về Optional<User>) cũng hữu ích nếu bạn
    // cho phép đăng nhập bằng email.
    // Optional<User> findByEmail(String email);

    long count();
}