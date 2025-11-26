package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.RegisterRequest;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; // Dùng @Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody; // Cần cho API

@Controller // Dùng @Controller (không phải @RestController) vì phục vụ cả HTML
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * [LIÊN KẾT VỚI SECURITY CONFIG]
     * GET /auth/login
     * Trả về trang login.html (Thymeleaf)
     */
    @GetMapping("/auth/login")
    public String loginPage() {
        return "login"; // Trả về /templates/login.html
    }

    /**
     * GET /auth/register
     * Trả về trang register.html (Thymeleaf)
     */
    @GetMapping("/auth/register")
    public String registerPage() {
        return "register"; // Trả về /templates/register.html
    }

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * POST /api/auth/register
     * API xử lý logic đăng ký tài khoản mới.
     * @ResponseBody là bắt buộc vì class này dùng @Controller
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // GỌI CHÍNH XÁC: userService.registerUser()
            UserResponse registeredUser = userService.registerUser(registerRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Bắt lỗi (ví dụ: username/email trùng) từ Service
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}