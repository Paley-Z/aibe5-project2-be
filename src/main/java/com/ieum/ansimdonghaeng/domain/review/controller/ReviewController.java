package com.ieum.ansimdonghaeng.domain.review.controller;

import com.ieum.ansimdonghaeng.common.response.ApiResponse;
import com.ieum.ansimdonghaeng.common.security.AuthenticatedUserSupport;
import com.ieum.ansimdonghaeng.common.security.CustomUserDetails;
import com.ieum.ansimdonghaeng.domain.review.dto.request.ReviewCreateRequest;
import com.ieum.ansimdonghaeng.domain.review.dto.request.ReviewUpdateRequest;
import com.ieum.ansimdonghaeng.domain.review.dto.response.ReviewDeleteResponse;
import com.ieum.ansimdonghaeng.domain.review.dto.response.ReviewEligibilityResponse;
import com.ieum.ansimdonghaeng.domain.review.dto.response.ReviewListResponse;
import com.ieum.ansimdonghaeng.domain.review.dto.response.ReviewResponse;
import com.ieum.ansimdonghaeng.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/projects/{projectId}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reviewService.createReview(
                        AuthenticatedUserSupport.currentUserId(userDetails),
                        projectId,
                        request
                )));
    }

    @GetMapping("/projects/{projectId}/reviews/eligibility")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewEligibilityResponse>> getReviewEligibility(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewEligibility(
                AuthenticatedUserSupport.currentUserId(userDetails),
                projectId
        )));
    }

    @PatchMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.updateReview(
                AuthenticatedUserSupport.currentUserId(userDetails),
                reviewId,
                request
        )));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewDeleteResponse>> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.deleteReview(
                AuthenticatedUserSupport.currentUserId(userDetails),
                reviewId
        )));
    }

    @GetMapping("/freelancers/{freelancerProfileId}/reviews")
    public ResponseEntity<ApiResponse<ReviewListResponse>> getFreelancerReviews(
            @PathVariable Long freelancerProfileId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getFreelancerReviews(freelancerProfileId, pageable)));
    }
}
