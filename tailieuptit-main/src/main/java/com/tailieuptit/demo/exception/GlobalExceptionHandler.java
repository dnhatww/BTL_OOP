package com.tailieuptit.demo.exception;

import com.tailieuptit.demo.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Bắt lỗi trên toàn bộ ứng dụng
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi 404 (Không tìm thấy tài nguyên)
     * Được kích hoạt khi Service ném ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        MessageResponse errorResponse = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Bắt lỗi 400 (Dữ liệu không hợp lệ)
     * Kích hoạt khi @Valid trên DTO (ví dụ: RegisterRequest) thất bại.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Trả về danh sách các lỗi
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt lỗi 403 (Không có quyền)
     * Kích hoạt khi Spring Security (@PreAuthorize) từ chối quyền truy cập.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<MessageResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        MessageResponse errorResponse = new MessageResponse("Bạn không có quyền thực hiện hành động này");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Bắt các lỗi liên quan đến Upload File
     */
    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<MessageResponse> handleFileUploadException(FileUploadException ex, WebRequest request) {
        MessageResponse errorResponse = new MessageResponse("Lỗi Upload File: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt lỗi 404 (Không tìm thấy tài nguyên)
     * Kích hoạt khi Spring không tìm thấy file hoặc API
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<MessageResponse> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        MessageResponse errorResponse = new MessageResponse("Không tìm thấy tài nguyên: " + ex.getResourcePath());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Bắt tất cả các lỗi 500 (Lỗi Máy chủ Chung)
     * Đây là "cái phễu" cuối cùng cho mọi lỗi không xác định.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<MessageResponse> handleGlobalException(Exception ex, WebRequest request) {
        // Log lỗi này ra console (rất quan trọng)
        ex.printStackTrace();

        MessageResponse errorResponse = new MessageResponse("Đã xảy ra lỗi không mong muốn: " + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}