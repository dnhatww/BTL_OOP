package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.DocumentDetailDTO;
import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.service.DocumentService;
// import com.tailieuptit.demo.service.CategoryService; // (Bạn sẽ cần Service này)
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Chỉ xử lý API (JSON)
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    // Giả sử bạn có CategoryService để lấy danh sách category
    // private final CategoryService categoryService;

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * GET /api/documents
     * Lấy danh sách tài liệu CÔNG KHAI (APPROVED) có phân trang
     * VD: ?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<DocumentSummaryDTO>> getPublicDocuments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        // GỌI CHÍNH XÁC: documentService.getApprovedDocuments()
        Page<DocumentSummaryDTO> documents = documentService.getApprovedDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * GET /api/documents/{id}
     * Lấy chi tiết 1 tài liệu CÔNG KHAI (APPROVED)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDocumentDetails(@PathVariable Long id) {
        try {
            // GỌI CHÍNH XÁC: documentService.getDocumentDetails()
            DocumentDetailDTO dto = documentService.getDocumentDetails(id);
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            // Bắt lỗi từ Service nếu không tìm thấy
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * GET /api/documents/search
     * Tìm kiếm tài liệu CÔNG KHAI
     * VD: ?keyword=java&categoryId=5
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DocumentSummaryDTO>> searchDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10) Pageable pageable) {

        // GỌI CHÍNH XÁC: documentService.searchDocuments()
        Page<DocumentSummaryDTO> documents = documentService.searchDocuments(keyword, categoryId, pageable);
        return ResponseEntity.ok(documents);
    }

    /* // API để lấy danh sách Category (dùng cho bộ lọc)
    @GetMapping("/api/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }
    */
}