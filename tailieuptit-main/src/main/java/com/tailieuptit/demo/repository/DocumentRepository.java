package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByIdAndStatus(Long id, DocumentStatus status);

    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);

    Page<Document> findByUserId(Long userId, Pageable pageable);

    long countByStatus(DocumentStatus status);
}