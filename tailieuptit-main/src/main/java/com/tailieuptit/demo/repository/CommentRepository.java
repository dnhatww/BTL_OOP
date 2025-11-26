package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * Hàm này được DocumentServiceImpl (hàm mapToDetailDTO) gọi
     * để lấy các comment GỐC (không phải reply) của một tài liệu.
     */
    List<Comment> findByDocumentIdAndParentIdIsNullOrderByCreatedAtDesc(Long documentId);

    Page<Comment> findByUserId(Long userId, Pageable pageable);
}