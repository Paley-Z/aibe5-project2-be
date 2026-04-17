package com.ieum.ansimdonghaeng.domain.review.dto.response;

public record ReviewEligibilityResponse(
        Long projectId,
        boolean canWrite,
        String reason,
        Long existingReviewId
) {
}
