package com.ieum.ansimdonghaeng.domain.proposal.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProposalCreateRequest(
        @Positive(message = "freelancerProfileId must be positive")
        Long freelancerProfileId,

        @Size(max = 2000, message = "message must be at most 2000 characters")
        String message
) {
}
