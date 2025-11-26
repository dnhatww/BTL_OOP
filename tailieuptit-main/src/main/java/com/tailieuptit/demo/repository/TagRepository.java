package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // Dùng để tìm 1 tag
    Optional<Tag> findByName(String name);

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE] (Trong tương lai)
     * Dùng khi bạn triển khai logic upload (hàm storeFile),
     * để tìm tất cả các tag đã tồn tại từ một danh sách tên.
     */
    Set<Tag> findByNameIn(Set<String> names);
}