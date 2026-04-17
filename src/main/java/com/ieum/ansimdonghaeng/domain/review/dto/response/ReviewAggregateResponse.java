package com.ieum.ansimdonghaeng.domain.review.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ReviewAggregateResponse(
        Long freelancerProfileId,
        BigDecimal averageRating,
        long totalReviews
) {

    public static ReviewAggregateResponse of(Long freelancerProfileId, Double averageRating, long totalReviews) {
        BigDecimal normalizedAverage = averageRating == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP);
        return new ReviewAggregateResponse(freelancerProfileId, normalizedAverage, totalReviews);
    }
}
