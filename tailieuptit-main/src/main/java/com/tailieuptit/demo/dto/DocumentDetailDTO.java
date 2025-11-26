package com.tailieuptit.demo.dto;

import com.tailieuptit.demo.entity.DocumentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class DocumentDetailDTO {

    // Các thông tin cơ bản (giống Summary)
    private Long id;
    private String title;
    private int viewsCount;
    private int downloadCount;
    private int commentsCount;
    private BigDecimal averageRating;
    private Timestamp createdAt;
    private String authorFullName;
    private String categoryName;

    // Thông tin chi tiết bổ sung
    private String description;
    private String mimeType;
    private DocumentStatus status; // (Quan trọng cho Admin xem)

    // Danh sách các quan hệ
    private Set<String> tags; // Chỉ cần trả về tên các Tag
    private List<CommentDTO> comments; // Danh sách các bình luận (gốc)
}