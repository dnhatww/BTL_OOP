package com.tailieuptit.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Tự động tạo Getter/Setter
public class CommentRequestDTO {

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(min = 1, max = 2000, message = "Bình luận phải dưới 2000 ký tự")
    private String content;

    // (Tùy chọn) Bạn có thể thêm trường này nếu muốn hỗ trợ reply
    // private Long parentId;
}