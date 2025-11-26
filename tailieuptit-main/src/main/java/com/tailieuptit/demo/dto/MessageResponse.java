package com.tailieuptit.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Tạo constructor với 1 tham số 'message'
public class MessageResponse {
    private String message;
}