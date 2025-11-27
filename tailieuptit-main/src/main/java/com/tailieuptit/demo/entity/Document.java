package com.tailieuptit.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_id")
    private Long id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath; // Đường dẫn/URL tới S3, GCS, hoặc file cục bộ

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    // Sử dụng Enum DocumentStatus
    @Enumerated(EnumType.STRING) // Lưu dưới dạng chuỗi ("PENDING", "APPROVED")
    @Column(name = "status", length = 20, nullable = false)
    private DocumentStatus status = DocumentStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    // --- Các cột tối ưu hóa (Denormalized) ---
    @Column(name = "views_count", nullable = false)
    private int viewsCount = 0;

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    @Column(name = "comments_count", nullable = false)
    private int commentsCount = 0;

    // --- Các Quan hệ (Relationships) ---

    // N-1 với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // N-1 với Category (nullable = true vì category_id có thể NULL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 1-N với Comment (Cascade khi xóa Document)
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();
}