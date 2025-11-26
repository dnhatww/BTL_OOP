package com.tailieuptit.demo.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.Set;

@Data // Tự động tạo Getter, Setter, toString, equals, hashCode
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private boolean isEnabled;
    private Timestamp createdAt;

    // Trả về tên của các vai trò (ví dụ: ["ROLE_USER", "ROLE_ADMIN"])
    private Set<String> roles;
}