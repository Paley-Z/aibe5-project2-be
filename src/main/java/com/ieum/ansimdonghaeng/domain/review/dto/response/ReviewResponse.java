package com.ieum.ansimdonghaeng.domain.review.dto.response;

import com.ieum.ansimdonghaeng.domain.review.entity.Review;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        Long projectId,
        Long reviewerUserId,
        String reviewerName,
        Integer rating,
        String content,
        boolean blinded,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProject().getId(),
                review.getReviewerUser().getId(),
                review.getReviewerUser().getName(),
                review.getRating(),
                review.getContent(),
                review.isBlinded(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
