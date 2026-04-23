package com.ieum.ansimdonghaeng.domain.review.dto.response;

import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.review.entity.Review;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import java.util.Objects;

final class ReviewResponseParticipantMapper {

    private ReviewResponseParticipantMapper() {
    }

    static String reviewDirection(Review review, Proposal acceptedProposal) {
        if (Objects.equals(review.getReviewerUserId(), review.getProject().getOwnerUserId())) {
            return "USER_TO_FREELANCER";
        }
        if (isAcceptedFreelancerReview(review, acceptedProposal)) {
            return "FREELANCER_TO_USER";
        }
        return null;
    }

    static Long revieweeUserId(Review review, Proposal acceptedProposal) {
        User reviewee = reviewee(review, acceptedProposal);
        return reviewee == null ? null : reviewee.getId();
    }

    static String revieweeName(Review review, Proposal acceptedProposal) {
        User reviewee = reviewee(review, acceptedProposal);
        return reviewee == null ? null : reviewee.getName();
    }

    static String revieweeRoleCode(Review review, Proposal acceptedProposal) {
        User reviewee = reviewee(review, acceptedProposal);
        return reviewee == null ? null : reviewee.getRoleCode();
    }

    private static User reviewee(Review review, Proposal acceptedProposal) {
        if (Objects.equals(review.getReviewerUserId(), review.getProject().getOwnerUserId())) {
            return acceptedProposal == null ? null : acceptedProposal.getFreelancerProfile().getUser();
        }
        if (isAcceptedFreelancerReview(review, acceptedProposal)) {
            return review.getProject().getOwnerUser();
        }
        return null;
    }

    private static boolean isAcceptedFreelancerReview(Review review, Proposal acceptedProposal) {
        return acceptedProposal != null
                && Objects.equals(
                        review.getReviewerUserId(),
                        acceptedProposal.getFreelancerProfile().getUser().getId()
                );
    }
}
