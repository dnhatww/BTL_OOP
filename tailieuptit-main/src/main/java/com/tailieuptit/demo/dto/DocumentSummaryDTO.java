package com.tailieuptit.demo.dto;

import com.tailieuptit.demo.entity.DocumentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class DocumentSummaryDTO {
    private Long id;
    private String title;
    private int viewsCount;
    private int downloadCount;
    private int commentsCount;
    private BigDecimal averageRating;
    private Timestamp createdAt;

    private DocumentStatus status;
    private String mimeType;

    // Thông tin bổ sung từ quan hệ
    private String authorFullName; // Tên người đăng
    private String categoryName;   // Tên danh mục
}