package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import com.tailieuptit.demo.dto.UserResponse;
import com.tailieuptit.demo.entity.DocumentStatus;
import com.tailieuptit.demo.repository.DocumentRepository;
import com.tailieuptit.demo.repository.UserRepository;
import com.tailieuptit.demo.service.DocumentService;
import com.tailieuptit.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin") // <-- 1. Đặt tiền tố chung là /admin
@PreAuthorize("hasRole('ADMIN')") // <-- 2. Khóa toàn bộ Class này, chỉ cho Admin
@RequiredArgsConstructor
public class AdminWebController {

    // 3. Inject các Service/Repository chỉ Admin mới cần
    private final UserService userService;
    private final DocumentService documentService;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    /**
     * [ADMIN DASHBOARD]
     * Xử lý: GET /admin/dashboard
     * Trả về: templates/admin/dashboard.html
     */
    @GetMapping("/dashboard")
    public String getAdminDashboard(Model model) {

        long totalUsers = userRepository.count();
        long approvedDocs = documentRepository.countByStatus(DocumentStatus.APPROVED);
        long pendingDocs = documentRepository.countByStatus(DocumentStatus.PENDING);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalApprovedDocs", approvedDocs);
        model.addAttribute("pendingDocsCount", pendingDocs);
        model.addAttribute("currentPage", "dashboard");
        model.addAttribute("title", "Bảng điều khiển");

        return "admin/dashboard";
    }

    /**
     * [ADMIN MODERATION]
     * Xử lý: GET /admin/moderation
     * Trả về: templates/admin/moderation.html
     */
    @GetMapping("/moderation")
    public String getModerationPage(Model model,
                                    @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<DocumentSummaryDTO> pendingDocs = documentService.getPendingDocuments(pageable);
        model.addAttribute("pendingDocuments", pendingDocs);
        model.addAttribute("currentPage", "moderation");

        return "admin/moderation";
    }

    /**
     * [ADMIN USER LIST]
     * Xử lý: GET /admin/users
     * Trả về: templates/admin/user-list.html
     */
    @GetMapping("/users")
    public String getUserListPage(Model model,
                                  @PageableDefault(size = 15, sort = "createdAt") Pageable pageable) {

        Page<UserResponse> usersPage = userService.getAllUsers(pageable);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", "users");

        return "admin/user-list";
    }
}