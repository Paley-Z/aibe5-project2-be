package com.ieum.ansimdonghaeng.domain.review.dto.response;

import com.ieum.ansimdonghaeng.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Page;

public record ReviewListResponse(
        List<ReviewResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        ReviewAggregateResponse aggregate
) {

    public static ReviewListResponse from(Page<Review> page, ReviewAggregateResponse aggregate) {
        return new ReviewListResponse(
                page.getContent().stream().map(ReviewResponse::from).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                aggregate
        );
    }
}
