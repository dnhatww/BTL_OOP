package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * [LIÊN KẾT VỚI INTERACTIONSERVICE]
     * Được gọi bởi addRating() để kiểm tra xem user đã đánh giá tài liệu này chưa.
     */
    Optional<Rating> findByDocumentIdAndUserId(Long documentId, Long userId);

    /**
     * [LIÊN KẾT VỚI INTERACTIONSERVICE]
     * Được gọi bởi addRating() để tính lại điểm trung bình (average_rating)
     * sau khi có đánh giá mới.
     */
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.document.id = :documentId")
    Double getAverageRatingByDocumentId(Long documentId);
}