package com.tailieuptit.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này được ném khi không tìm thấy tài nguyên (lỗi 404)
 * Ví dụ: DocumentServiceImpl ném lỗi này nếu không tìm thấy Document
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Tự động trả về mã 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}