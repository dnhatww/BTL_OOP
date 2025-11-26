package com.tailieuptit.demo.service;

import com.tailieuptit.demo.dto.RegisterRequest;
import com.tailieuptit.demo.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    /**
     * Ghi chú: UserDetailsService đã cung cấp:
     * UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
     */

    /**
     * Xử lý logic đăng ký người dùng mới.
     * @param registerRequest DTO chứa thông tin đăng ký
     * @return UserResponse DTO của người dùng vừa tạo
     * @throws Exception nếu username hoặc email đã tồn tại
     */
    UserResponse registerUser(RegisterRequest registerRequest) throws Exception;

    /**
     * Tìm thông tin người dùng bằng username.
     * @param username Tên đăng nhập
     * @return UserResponse DTO
     */
    UserResponse findByUsername(String username);

    /**
     * [ADMIN] Lấy danh sách tất cả người dùng (phân trang).
     * @param pageable Thông tin phân trang
     * @return Trang (Page) chứa UserResponse DTO
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * [ADMIN] Vô hiệu hóa hoặc xóa một người dùng.
     * @param userId ID của người dùng cần vô hiệu hóa
     */
    void disableUser(Long userId);
}