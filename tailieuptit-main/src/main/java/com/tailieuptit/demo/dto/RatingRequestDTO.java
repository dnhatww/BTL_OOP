package com.tailieuptit.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data // Tự động tạo Getter/Setter
public class RatingRequestDTO {

    @Min(value = 1, message = "Điểm đánh giá phải ít nhất là 1")
    @Max(value = 5, message = "Điểm đánh giá không được quá 5")
    private short score; // Dùng 'short' cho điểm từ 1-5
}