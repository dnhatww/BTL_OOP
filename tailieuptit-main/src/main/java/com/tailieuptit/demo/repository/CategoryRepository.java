package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Dùng để tìm kiếm (ví dụ: /category/lap-trinh-java)
    Optional<Category> findBySlug(String slug);

    // Dùng để kiểm tra khi tạo/sửa
    Optional<Category> findByName(String name);
}