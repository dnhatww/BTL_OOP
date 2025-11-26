package com.tailieuptit.demo.exception;

/**
 * Exception này được ném khi có lỗi trong quá trình xử lý file
 * Ví dụ: FileStorageServiceImpl ném lỗi này
 */
public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}