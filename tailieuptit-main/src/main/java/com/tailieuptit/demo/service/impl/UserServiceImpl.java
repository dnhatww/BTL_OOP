package com.tailieuptit.demo.service.impl;

import com.tailieuptit.demo.dto.RegisterRequest;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.entity.Role;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.repository.RoleRepository;
import com.tailieuptit.demo.repository.UserRepository;
import com.tailieuptit.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động inject các dependency (final)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [LIÊN KẾT VỚI SPRING SECURITY]
     * Spring Security sẽ gọi hàm này khi user đăng nhập
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // GỌI REPO: findByUsername
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));
    }

    /**
     * [LIÊN KẾT VỚI AUTH CONTROLLER]
     */
    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) throws Exception {
        // GỌI REPO: existsByUsername
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new Exception("Tên đăng nhập đã tồn tại");
        }
        // GỌI REPO: existsByEmail
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new Exception("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setEnabled(true);

        // GỌI REPO: findByName (để gán vai trò mặc định)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ROLE_USER."));
        user.setRoles(Set.of(userRole));

        // GỌI REPO: save
        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + username));
        return mapToUserResponse(user);
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        // GỌI REPO: findAll
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::mapToUserResponse); // Chuyển Page<User> -> Page<UserResponse>
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional
    public void disableUser(Long userId) {
        // GỌI REPO: findById
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + userId));

        user.setEnabled(false); // Vô hiệu hóa
        // GỌI REPO: save
        userRepository.save(user);
    }

    // --- Helper ---
    // Hàm nội bộ để chuyển Entity -> DTO (tránh lộ password)
    private UserResponse mapToUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return dto;
    }
}