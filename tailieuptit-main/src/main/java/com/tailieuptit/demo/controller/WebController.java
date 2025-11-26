package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.DocumentDetailDTO;
import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.repository.CategoryRepository; // Dùng để lấy list category
import com.tailieuptit.demo.service.DocumentService;
import com.tailieuptit.demo.service.InteractionService;
import com.tailieuptit.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // <-- DÙNG @Controller (KHÔNG PHẢI @RestController)
@RequiredArgsConstructor
public class WebController {

    // Inject tất cả Service cần thiết để lấy dữ liệu
    private final DocumentService documentService;
    private final UserService userService;
    private final InteractionService interactionService;
    private final CategoryRepository categoryRepository;

    /**
     * [TRANG CHỦ]
     * Xử lý: GET /
     * Trả về: templates/index.html
     */
    @GetMapping("/")
    public String getHomePage(Model model,
                              @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. Gọi Service lấy dữ liệu
        Page<DocumentSummaryDTO> documentPage = documentService.getApprovedDocuments(pageable);

        // 2. Nhét dữ liệu vào Model cho Thymeleaf
        model.addAttribute("documents", documentPage); // Biến 'documents' cho index.html
        model.addAttribute("pageTitle", "Tài Liệu Mới Nhất");

        return "index"; // 3. Trả về file "index.html"
    }

    /**
     * [TRANG TÌM KIẾM]
     * Xử lý: GET /search
     * Trả về: templates/index.html (Tái sử dụng)
     */
    @GetMapping("/search")
    public String getSearchPage(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model,
            @PageableDefault(size = 9) Pageable pageable) {

        Page<DocumentSummaryDTO> documentPage = documentService.searchDocuments(keyword, categoryId, pageable);
        model.addAttribute("documents", documentPage);
        model.addAttribute("pageTitle", "Kết quả tìm kiếm cho: '" + keyword + "'");
        model.addAttribute("searchQuery", keyword); // Giữ lại từ khóa trong ô search

        return "index"; // Vẫn trả về "index.html"
    }

    /**
     * [TRANG CHI TIẾT TÀI LIỆU]
     * Xử lý: GET /document/{id}
     * Trả về: templates/document-detail.html
     */
    @GetMapping("/document/{id}")
    public String getDocumentDetailPage(@PathVariable Long id, Model model) {
        try {
            // 1. Gọi Service
            DocumentDetailDTO doc = documentService.getDocumentDetails(id);
            // 2. Nhét vào Model
            model.addAttribute("document", doc); // Biến 'document' cho document-detail.html
            // 3. Trả về file
            return "document-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Không tìm thấy tài liệu.");
            return "redirect:/"; // Về trang chủ nếu lỗi
        }
    }

    /**
     * [TRANG UPLOAD]
     * Xử lý: GET /document/upload
     * Trả về: templates/upload.html
     */
    @GetMapping("/document/upload")
    @PreAuthorize("isAuthenticated()") // Yêu cầu đăng nhập
    public String getUploadPage(Model model) {
        // Trang upload.html cần danh sách Category để hiển thị <select>
        model.addAttribute("categories", categoryRepository.findAll());
        return "upload"; // Trả về "upload.html"
    }

    @GetMapping("/about")
    public String getAbout() {
        return "about"; // Trả về "about.html"
    }

    /**
     * [TRANG HỒ SƠ CÁ NHÂN]
     * Xử lý: GET /profile
     * Trả về: templates/profile.html
     * (Đây là hàm đã fix lỗi 404 của bạn)
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // Yêu cầu đăng nhập
    public String getProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Lấy thông tin User (DTO)
        UserResponse user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);

        // 2. Lấy 5 tài liệu mới nhất của User
        Pageable docPageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        model.addAttribute("userDocuments", documentService.findDocumentsByUserId(user.getId(), docPageable));

        // 3. (Tương tự) Lấy 5 bình luận mới nhất
        Pageable commentPageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        model.addAttribute("userComments", interactionService.findCommentsByUserId(user.getId(), commentPageable));

        return "profile";
    }
}