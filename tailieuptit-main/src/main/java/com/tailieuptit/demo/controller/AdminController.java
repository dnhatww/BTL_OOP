package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import com.tailieuptit.demo.dto.MessageResponse;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.service.DocumentService;
import com.tailieuptit.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin") // Tiền tố chung cho Admin
@PreAuthorize("hasRole('ADMIN')") // Khóa TOÀN BỘ class này, chỉ cho phép ADMIN
@RequiredArgsConstructor
public class AdminController {

    private final DocumentService documentService;
    private final UserService userService;

    // --- Quản lý Tài liệu ---

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * GET /api/admin/documents/pending
     * Lấy danh sách tài liệu CHỜ DUYỆT (PENDING)
     */
    @GetMapping("/documents/pending")
    public ResponseEntity<Page<DocumentSummaryDTO>> getPendingDocuments(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        // GỌI CHÍNH XÁC: documentService.getPendingDocuments()
        Page<DocumentSummaryDTO> documents = documentService.getPendingDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * POST /api/admin/documents/{id}/approve
     * Phê duyệt tài liệu
     */
    @PostMapping("/documents/{id}/approve")
    public ResponseEntity<MessageResponse> approveDocument(@PathVariable Long id) {
        // GỌI CHÍNH XÁC: documentService.approveDocument()
        documentService.approveDocument(id);
        return ResponseEntity.ok(new MessageResponse("Phê duyệt tài liệu thành công!"));
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * POST /api/admin/documents/{id}/reject
     * Từ chối tài liệu
     */
    @PostMapping("/documents/{id}/reject")
    public ResponseEntity<MessageResponse> rejectDocument(@PathVariable Long id) {
        // GỌI CHÍNH XÁC: documentService.rejectDocument()
        documentService.rejectDocument(id);
        return ResponseEntity.ok(new MessageResponse("Từ chối tài liệu thành công!"));
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * DELETE /api/admin/documents/{id}
     * Xóa 1 tài liệu (bất kể của ai)
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<MessageResponse> deleteDocumentAsAdmin(@PathVariable Long id) {
        // GỌI CHÍNH XÁC: documentService.deleteDocumentAsAdmin()
        documentService.deleteDocumentAsAdmin(id);
        return ResponseEntity.ok(new MessageResponse("Xóa tài liệu thành công!"));
    }

    // --- Quản lý Người dùng ---

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * GET /api/admin/users
     * Lấy danh sách tất cả người dùng
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {

        // GỌI CHÍNH XÁC: userService.getAllUsers()
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * DELETE /api/admin/users/{id}
     * Vô hiệu hóa hoặc xóa 1 người dùng
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> disableUser(@PathVariable Long id) {
        // GỌI CHÍNH XÁC: userService.disableUser()
        userService.disableUser(id);
        return ResponseEntity.ok(new MessageResponse("Vô hiệu hóa user thành công!"));
    }
}