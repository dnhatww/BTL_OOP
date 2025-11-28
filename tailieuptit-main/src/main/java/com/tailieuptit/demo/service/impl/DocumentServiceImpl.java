package com.tailieuptit.demo.service.impl;

import com.tailieuptit.demo.dto.CommentDTO;
import com.tailieuptit.demo.dto.DocumentDetailDTO;
import com.tailieuptit.demo.dto.DocumentSummaryDTO;
import com.tailieuptit.demo.entity.*;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.repository.CategoryRepository;
import com.tailieuptit.demo.repository.DocumentRepository;
import com.tailieuptit.demo.repository.UserRepository;
import com.tailieuptit.demo.service.DocumentService;
import com.tailieuptit.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional
    public void uploadDocument(MultipartFile file, String title, String description, Long categoryId, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + username));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Category: " + categoryId));

        String storedFilename = fileStorageService.storeFile(file);

        Document doc = new Document();
        doc.setTitle(title);
        doc.setDescription(description);
        doc.setFilePath(storedFilename); // Lưu tên file duy nhất
        doc.setMimeType(file.getContentType());
        doc.setUser(user);
        doc.setCategory(category);
        doc.setStatus(DocumentStatus.PENDING); // Chờ duyệt

        documentRepository.save(doc);
    }

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional
    public Resource downloadDocument(Long docId, String username) throws Exception {
        Document doc = documentRepository.findByIdAndStatus(docId, DocumentStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu hoặc chưa được duyệt: " + docId));

        // Tăng đếm lượt download
        doc.setDownloadCount(doc.getDownloadCount() + 1);
        documentRepository.save(doc);

        return fileStorageService.loadFileAsResource(doc.getFilePath());
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT CONTROLLER]
     */
    @Override
    @Transactional
    public DocumentDetailDTO getDocumentDetails(Long docId) throws Exception {
        Document doc = documentRepository.findByIdAndStatus(docId, DocumentStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));

        // Tăng đếm lượt xem
        doc.setViewsCount(doc.getViewsCount() + 1);
        documentRepository.save(doc);

        return mapToDetailDTO(doc);
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT CONTROLLER]
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSummaryDTO> getApprovedDocuments(Pageable pageable) {
        Page<Document> docPage = documentRepository.findByStatus(DocumentStatus.APPROVED, pageable);
        return docPage.map(this::mapToSummaryDTO);
    }

    /**
     * [LIÊN KẾT VỚI DOCUMENT CONTROLLER]
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSummaryDTO> searchDocuments(String keyword, Long categoryId, Pageable pageable) {

        Specification<Document> spec = Specification.where(
                (root, query, cb) -> cb.equal(root.get("status"), DocumentStatus.APPROVED)
        );

        if (StringUtils.hasText(keyword)) {
            String kw = "%" + keyword.toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                    cb.or(
                            // Tìm theo tiêu đề
                            cb.like(cb.lower(root.get("title")), kw),

                            // Tìm theo mô tả
                            cb.like(cb.lower(root.get("description")), kw),

                            // Tìm theo tên người đăng
                            cb.like(cb.lower(root.join("user").get("fullName")), kw),

                            // Tìm theo tên danh mục
                            cb.like(cb.lower(root.join("category").get("name")), kw)
                    )
            );
        }

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId)
            );
        }

        Page<Document> docPage = documentRepository.findAll(spec, pageable);
        return docPage.map(this::mapToSummaryDTO);
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSummaryDTO> getPendingDocuments(Pageable pageable) {
        Page<Document> docPage = documentRepository.findByStatus(DocumentStatus.PENDING, pageable);
        return docPage.map(this::mapToSummaryDTO);
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional
    public void approveDocument(Long docId) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));
        doc.setStatus(DocumentStatus.APPROVED);
        documentRepository.save(doc);
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional
    public void rejectDocument(Long docId) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));
        doc.setStatus(DocumentStatus.REJECTED);
        documentRepository.save(doc);
    }

    /**
     * [LIÊN KẾT VỚI ADMIN CONTROLLER]
     */
    @Override
    @Transactional
    public void deleteDocumentAsAdmin(Long docId) {
        Document doc = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));

        try {
            fileStorageService.deleteFile(doc.getFilePath());
        } catch (Exception e) {
            // Log lỗi (ví dụ: file không tồn tại) nhưng vẫn tiếp tục xóa DB
            System.err.println("Lỗi xóa file: " + doc.getFilePath() + ". Lỗi: " + e.getMessage());
        }

        documentRepository.delete(doc); // Cascade sẽ xóa comments, ratings, ...
    }


    // --- Helpers (Hàm nội bộ) ---

    private DocumentSummaryDTO mapToSummaryDTO(Document doc) {
        DocumentSummaryDTO dto = new DocumentSummaryDTO();
        dto.setId(doc.getId());
        dto.setTitle(doc.getTitle());
        dto.setViewsCount(doc.getViewsCount());
        dto.setDownloadCount(doc.getDownloadCount());
        dto.setCommentsCount(doc.getCommentsCount());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setStatus(doc.getStatus());
        dto.setMimeType(doc.getMimeType());

        // Tránh lỗi NullPointerException nếu user hoặc category bị null
        if (doc.getUser() != null) {
            dto.setAuthorFullName(doc.getUser().getFullName());
        }
        if (doc.getCategory() != null) {
            dto.setCategoryName(doc.getCategory().getName());
        }
        return dto;
    }

    private DocumentDetailDTO mapToDetailDTO(Document doc) {
        DocumentDetailDTO dto = new DocumentDetailDTO();

        // Copy các trường từ Summary
        dto.setId(doc.getId());
        dto.setTitle(doc.getTitle());
        dto.setViewsCount(doc.getViewsCount());
        dto.setDownloadCount(doc.getDownloadCount());
        dto.setCommentsCount(doc.getCommentsCount());
        dto.setCreatedAt(doc.getCreatedAt());

        if (doc.getUser() != null) {
            dto.setAuthorFullName(doc.getUser().getFullName());
        }
        if (doc.getCategory() != null) {
            dto.setCategoryName(doc.getCategory().getName());
        }

        // Thêm các trường chi tiết
        dto.setDescription(doc.getDescription());
        dto.setMimeType(doc.getMimeType());
        dto.setStatus(doc.getStatus());

        // Map Comments (Chỉ lấy comment gốc, replies sẽ được xử lý lồng nhau)
        dto.setComments(doc.getComments().stream()
                .filter(c -> c.getParent() == null) // Chỉ lấy comment gốc
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getUser() != null) {
            dto.setFullName(comment.getUser().getFullName());
            dto.setUserId(comment.getUser().getId());
        }

        // Xử lý đệ quy cho replies
        dto.setReplies(comment.getReplies().stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentSummaryDTO> findDocumentsByUserId(Long userId, Pageable pageable) {
        Page<Document> docPage = documentRepository.findByUserId(userId, pageable);
        // Chuyển sang DTO
        return docPage.map(this::mapToSummaryDTO); // Tận dụng lại hàm helper
    }
}