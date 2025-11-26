package com.tailieuptit.demo.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {

    /**
     * Lưu file upload lên hệ thống lưu trữ (local, S3, ...).
     * @param file File người dùng upload
     * @return Tên file duy nhất được tạo ra để lưu trong DB (ví dụ: "uuid.pdf")
     * @throws Exception nếu lưu file thất bại
     */
    String storeFile(MultipartFile file) throws Exception;

    /**
     * Lấy file dưới dạng Resource để download.
     * @param filename Tên file đã lưu (tên duy nhất từ DB)
     * @return Một đối tượng Resource
     * @throws Exception nếu không tìm thấy file
     */
    Resource loadFileAsResource(String filename) throws Exception;

    /**
     * Xóa file khỏi hệ thống lưu trữ.
     * @param filename Tên file đã lưu
     * @throws Exception nếu xóa thất bại
     */
    void deleteFile(String filename) throws Exception;

    /**
     * (Tùy chọn) Lấy đường dẫn vật lý của file.
     * @param filename Tên file
     * @return Path
     */
    Path load(String filename);
}