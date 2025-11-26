package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * Được gọi bởi registerUser() để gán vai trò mặc định (ROLE_USER)
     * cho người dùng mới.
     */
    Optional<Role> findByName(String name);
}