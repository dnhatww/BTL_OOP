package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

/**
 * Kế thừa JpaSpecificationExecutor để hỗ trợ tìm kiếm động (Specification)
 * [LIÊN KẾT VỚI DOCUMENTSERVICE]
 * Được DocumentServiceImpl (hàm searchDocuments) gọi
 */
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * Được gọi bởi:
     * 1. getDocumentDetails() - Lấy chi tiết tài liệu (chỉ APPROVED)
     * 2. downloadDocument() - Chỉ cho phép tải file (chỉ APPROVED)
     */
    Optional<Document> findByIdAndStatus(Long id, DocumentStatus status);

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * Được gọi bởi:
     * 1. getApprovedDocuments() - Lấy tài liệu CÔNG KHAI
     * 2. getPendingDocuments() - Lấy tài liệu CHỜ DUYỆT (cho Admin)
     */
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    Page<Document> findByUserId(Long userId, Pageable pageable);

    long countByStatus(DocumentStatus status);
}