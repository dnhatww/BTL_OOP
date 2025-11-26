package com.tailieuptit.demo.service.impl;

import com.tailieuptit.demo.exception.FileUploadException;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    // Lấy giá trị từ application.properties
    public FileStorageServiceImpl(@Value("${app.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /**
     * Hàm này được gọi ngay sau khi Service được tạo
     * để tạo thư mục /uploads nếu chưa có
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileUploadException("Không thể tạo thư mục để lưu file.", ex);
        }
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT SERVICE]
     */
    @Override
    public String storeFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new FileUploadException("Không thể lưu file rỗng.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // Tạo tên file duy nhất (UUID + tên gốc)
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new FileUploadException("Lưu file thất bại: " + uniqueFilename, ex);
        }
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT SERVICE]
     */
    @Override
    public Resource loadFileAsResource(String filename) throws Exception {
        try {
            Path filePath = load(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Không thể đọc file: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Không thể đọc file: " + filename, ex);
        }
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT SERVICE]
     */
    @Override
    public void deleteFile(String filename) throws Exception {
        try {
            Path filePath = load(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Có thể log lỗi nhưng không cần ném exception
            System.err.println("Xóa file thất bại: " + filename);
        }
    }

    @Override
    public Path load(String filename) {
        return this.fileStorageLocation.resolve(filename);
    }
}