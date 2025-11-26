package com.tailieuptit.demo.service;

import com.tailieuptit.demo.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InteractionService {

    /**
     * [USER] Thêm một bình luận mới vào tài liệu.
     * @param docId ID tài liệu
     * @param content Nội dung bình luận
     * @param username Tên người dùng bình luận
     */
    void addComment(Long docId, String content, String username);

    /**
     * [USER] Thêm hoặc cập nhật đánh giá (rating) cho tài liệu.
     * (Hệ thống sẽ tự tính lại average_rating của Document)
     * @param docId ID tài liệu
     * @param score Điểm (1-5)
     * @param username Tên người dùng đánh giá
     */
    void addRating(Long docId, short score, String username);

    /**
     * [USER/ADMIN] Xóa một bình luận.
     * @param commentId ID bình luận
     * @param username Tên người dùng yêu cầu xóa (để kiểm tra quyền sở hữu)
     * @throws Exception nếu không có quyền xóa
     */
    void deleteComment(Long commentId, String username) throws Exception;

    Page<CommentDTO> findCommentsByUserId(Long userId, Pageable pageable);
}