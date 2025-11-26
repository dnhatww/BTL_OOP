package com.tailieuptit.demo.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Lớp tiện ích để truy cập thông tin bảo mật (Security Context).
 */
public class SecurityUtil {

    /**
     * Lấy username của người dùng đang đăng nhập hiện tại.
     *
     * @return Optional<String> chứa username, hoặc Optional.empty() nếu không có ai đăng nhập.
     */
    public static Optional<String> getCurrentUsername() {
        // Lấy đối tượng Authentication từ Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu không có ai đăng nhập hoặc là người dùng 'anonymous'
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // Lấy username từ UserDetails (chuẩn của Spring Security)
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        }
        // Hoặc lấy từ tên của principal
        else {
            return Optional.of(principal.toString());
        }
    }
}