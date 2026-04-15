package com.ieum.ansimdonghaeng.domain.proposal.dto.response;

import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;

public record ProposalCreateResponse(
        Long proposalId,
        Long projectId,
        Long freelancerProfileId,
        ProposalStatus status
) {

    // 제안 생성 직후 필요한 식별자와 상태만 간단히 반환한다.
    public static ProposalCreateResponse from(Proposal proposal) {
        return new ProposalCreateResponse(
                proposal.getId(),
                proposal.getProject().getId(),
                proposal.getFreelancerProfile().getId(),
                proposal.getStatus()
        );
    }
}
