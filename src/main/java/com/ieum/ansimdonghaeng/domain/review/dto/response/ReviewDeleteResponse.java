package com.ieum.ansimdonghaeng.domain.review.dto.response;

public record ReviewDeleteResponse(
        Long reviewId,
        boolean deleted
) {
}
