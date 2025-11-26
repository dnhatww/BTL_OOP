package com.tailieuptit.demo.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private String fullName; // Tên người bình luận
    private Long userId;     // ID người bình luận (để kiểm tra xóa/sửa)
    private Timestamp createdAt;

    // Dùng cho bình luận lồng nhau
    private List<CommentDTO> replies;

    private Long documentId;
    private String documentTitle;
}