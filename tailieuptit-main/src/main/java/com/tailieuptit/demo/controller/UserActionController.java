package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.CommentRequestDTO;
import com.tailieuptit.demo.dto.MessageResponse;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.service.DocumentService;
import com.tailieuptit.demo.service.InteractionService;
import com.tailieuptit.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api") // Dùng tiền tố chung /api
@RequiredArgsConstructor
public class UserActionController {

    private final DocumentService documentService;
    private final InteractionService interactionService;
    private final UserService userService;

    /**
     * [LIÊN KẾT VỚI USERSERVICE]
     * GET /api/users/me
     * Lấy thông tin của user đang đăng nhập
     */
    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()") // Yêu cầu đã đăng nhập (USER hoặc ADMIN)
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // GỌI CHÍNH XÁC: userService.findByUsername()
        UserResponse userResponse = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * POST /api/documents/upload
     * Upload tài liệu
     */
    @PostMapping("/documents/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // GỌI CHÍNH XÁC: documentService.uploadDocument()
            documentService.uploadDocument(
                    file,
                    title,
                    description,
                    categoryId,
                    userDetails.getUsername() // Lấy username từ Spring Security
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Upload thành công! Tài liệu đang chờ duyệt."));
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENTSERVICE]
     * GET /api/documents/download/{id}
     * Tải file tài liệu
     */
    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        // Lấy file từ service
        Resource resource = documentService.downloadDocument(id, userDetails.getUsername());

        // Lấy tên file thật đang lưu trên ổ
        String filename = resource.getFilename();

        // Encode UTF-8 để tránh lỗi ký tự lạ (FIX CHÍNH)
        String encodedFilename = java.net.URLEncoder
                .encode(filename, "UTF-8")
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + encodedFilename +
                                "\"; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    /**
     * [LIÊN KẾT VỚI INTERACTIONSERVICE]
     * POST /api/documents/{id}/comment
     * Gửi bình luận
     */
    @PostMapping("/documents/{id}/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO commentRequest, // Giả sử có DTO này
            @AuthenticationPrincipal UserDetails userDetails) {

        // GỌI CHÍNH XÁC: interactionService.addComment()
        interactionService.addComment(id, commentRequest.getContent(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Bình luận thành công!"));
    }

    /**
     * [LIÊN KẾT VỚI INTERACTIONSERVICE]
     * DELETE /api/comments/{id}
     * Xóa 1 bình luận (chỉ chủ sở hữu hoặc Admin)
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // GỌI CHÍNH XÁC: interactionService.deleteComment()
            interactionService.deleteComment(id, userDetails.getUsername());
            return ResponseEntity.ok(new MessageResponse("Xóa bình luận thành công!"));
        } catch (Exception e) {
            // Bắt lỗi AccessDeniedException hoặc ResourceNotFoundException
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }
}