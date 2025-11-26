package com.tailieuptit.demo.service;

import com.tailieuptit.demo.dto.DocumentDetailDTO;
import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    /**
     * [USER] Xử lý logic upload tài liệu.
     * @param file File upload
     * @param title Tiêu đề
     * @param description Mô tả
     * @param categoryId ID của Category
     * @param username Tên người dùng đang upload
     * @throws Exception nếu có lỗi (upload, validation)
     */
    void uploadDocument(MultipartFile file, String title, String description, Long categoryId, String username) throws Exception;

    /**
     * [USER] Xử lý logic download tài liệu.
     * @param docId ID của tài liệu
     * @param username Tên người dùng đang download (để kiểm tra quyền, log)
     * @return Resource của file
     * @throws Exception nếu không tìm thấy hoặc không có quyền
     */
    Resource downloadDocument(Long docId, String username) throws Exception;

    /**
     * [PUBLIC] Lấy chi tiết một tài liệu (đã APPROVED).
     * @param docId ID tài liệu
     * @return DocumentDetailDTO
     * @throws Exception (ResourceNotFoundException) nếu không tìm thấy
     */
    DocumentDetailDTO getDocumentDetails(Long docId) throws Exception;

    /**
     * [PUBLIC] Lấy danh sách tài liệu (đã APPROVED) có phân trang.
     * @param pageable Thông tin phân trang
     * @return Trang (Page) chứa DocumentSummaryDTO
     */
    Page<DocumentSummaryDTO> getApprovedDocuments(Pageable pageable);

    /**
     * [PUBLIC] Tìm kiếm tài liệu (đã APPROVED).
     * @param keyword Từ khóa (có thể null)
     * @param categoryId ID Category (có thể null)
     * @param pageable Thông tin phân trang
     * @return Trang (Page) chứa DocumentSummaryDTO
     */
    Page<DocumentSummaryDTO> searchDocuments(String keyword, Long categoryId, Pageable pageable);

    /**
     * [ADMIN] Lấy danh sách tài liệu đang chờ duyệt (PENDING).
     * @param pageable Thông tin phân trang
     * @return Trang (Page) chứa DocumentSummaryDTO
     */
    Page<DocumentSummaryDTO> getPendingDocuments(Pageable pageable);

    /**
     * [ADMIN] Phê duyệt một tài liệu.
     * @param docId ID tài liệu
     */
    void approveDocument(Long docId);

    /**
     * [ADMIN] Từ chối một tài liệu.
     * @param docId ID tài liệu
     */
    void rejectDocument(Long docId);

    /**
     * [ADMIN] Xóa một tài liệu (do Admin xóa).
     * @param docId ID tài liệu
     */
    void deleteDocumentAsAdmin(Long docId);

    Page<DocumentSummaryDTO> findDocumentsByUserId(Long userId, Pageable pageable);
}