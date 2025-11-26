package com.tailieuptit.demo.service.impl;

import com.tailieuptit.demo.dto.CommentDTO;
import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.Rating;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.exception.ResourceNotFoundException;
import com.tailieuptit.demo.repository.CommentRepository;
import com.tailieuptit.demo.repository.DocumentRepository;
import com.tailieuptit.demo.repository.RatingRepository;
import com.tailieuptit.demo.repository.UserRepository;
import com.tailieuptit.demo.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
//import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional
    public void addComment(Long docId, String content, String username) {
        // GỌI REPO: findById (Document)
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));
        // GỌI REPO: findByUsername
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + username));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setDocument(document);
        comment.setUser(user);

        // GỌI REPO: save (Comment)
        commentRepository.save(comment);

        // [LIÊN KẾT TỐI ƯU HÓA]
        // Cập nhật cột 'comments_count' trong bảng Document
        document.setCommentsCount(document.getCommentsCount() + 1);
        documentRepository.save(document);
    }

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional
    public void addRating(Long docId, short score, String username) {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài liệu: " + docId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + username));

        // Kiểm tra xem user đã rate chưa
        // GỌI REPO: findByDocumentIdAndUserId
        Optional<Rating> existingRating = ratingRepository.findByDocumentIdAndUserId(docId, user.getId());

        if (existingRating.isPresent()) {
            // Nếu đã rate -> Cập nhật
            Rating rating = existingRating.get();
            rating.setScore(score);
            ratingRepository.save(rating);
        } else {
            // Nếu chưa rate -> Tạo mới
            Rating newRating = new Rating();
            newRating.setScore(score);
            newRating.setDocument(document);
            newRating.setUser(user);
            ratingRepository.save(newRating);
        }

        // [LIÊN KẾT TỐI ƯU HÓA]
        // Cập nhật 'average_rating' trong bảng Document
        // GỌI REPO: getAverageRatingByDocumentId (Hàm @Query trong RatingRepository)
        Double avgRating = ratingRepository.getAverageRatingByDocumentId(docId);
        if (avgRating != null) {
//            BigDecimal roundedAvg = BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP);
            BigDecimal roundedAvg = BigDecimal.valueOf(avgRating).setScale(2, BigDecimal.ROUND_HALF_UP);
            document.setAverageRating(roundedAvg);
            documentRepository.save(document);
        }
    }

    /**
     * [LIÊN KẾT VỚI USER ACTION CONTROLLER]
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) throws Exception {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận: " + commentId));
        User requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user: " + username));

        // Kiểm tra quyền: Hoặc là chủ comment, hoặc là ADMIN
        boolean isAdmin = requestingUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        boolean isOwner = comment.getUser().getId().equals(requestingUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không có quyền xóa bình luận này");
        }

        // GỌI REPO: delete
        commentRepository.delete(comment);

        // [LIÊN KẾT TỐI ƯU HÓA]
        // Cập nhật (giảm) 'comments_count'
        Document document = comment.getDocument();
        document.setCommentsCount(Math.max(0, document.getCommentsCount() - 1));
        documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> findCommentsByUserId(Long userId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByUserId(userId, pageable);
        return commentPage.map(this::mapToCommentDTOForProfile);
    }

    private CommentDTO mapToCommentDTOForProfile(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setFullName(comment.getUser().getFullName());
        }
        if (comment.getDocument() != null) {
            dto.setDocumentId(comment.getDocument().getId());
            dto.setDocumentTitle(comment.getDocument().getTitle());
        }
        return dto;
    }
}