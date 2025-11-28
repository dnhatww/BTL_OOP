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

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * GET /auth/login
     * Trả về trang login.html
     */
    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }

    /**
     * GET /auth/register
     * Trả về trang register.html
     */
    @GetMapping("/auth/register")
    public String registerPage() {
        return "register";
    }

    /**
     * POST /api/auth/register
     * API xử lý logic đăng ký tài khoản mới.
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UserResponse registeredUser = userService.registerUser(registerRequest);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}