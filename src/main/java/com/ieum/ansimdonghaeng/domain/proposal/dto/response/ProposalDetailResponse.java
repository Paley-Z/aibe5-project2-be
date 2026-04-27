package com.ieum.ansimdonghaeng.domain.proposal.dto.response;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import java.time.LocalDateTime;

public record ProposalDetailResponse(
        Long proposalId,
        Long projectId,
        Long ownerUserId,
        String ownerName,
        String projectTitle,
        String projectTypeCode,
        String serviceRegionCode,
        LocalDateTime requestedStartAt,
        LocalDateTime requestedEndAt,
        String serviceAddress,
        String serviceDetailAddress,
        String requestDetail,
        Long freelancerProfileId,
        Long freelancerUserId,
        String freelancerName,
        ProposalStatus proposalStatus,
        ProjectStatus projectStatus,
        String message,
        LocalDateTime respondedAt,
        LocalDateTime projectAcceptedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // 상세 응답에서는 제안과 연결된 프로젝트 정보를 함께 내려준다.
    public static ProposalDetailResponse from(Proposal proposal) {
        String ownerName = proposal.getProject().getOwnerUser() == null
                ? null
                : proposal.getProject().getOwnerUser().getName();
        return from(proposal, ownerName);
    }

    public static ProposalDetailResponse from(Proposal proposal, String ownerName) {
        return new ProposalDetailResponse(
                proposal.getId(),
                proposal.getProject().getId(),
                proposal.getProject().getOwnerUserId(),
                ownerName,
                proposal.getProject().getTitle(),
                proposal.getProject().getProjectTypeCode(),
                proposal.getProject().getServiceRegionCode(),
                proposal.getProject().getRequestedStartAt(),
                proposal.getProject().getRequestedEndAt(),
                proposal.getProject().getServiceAddress(),
                proposal.getProject().getServiceDetailAddress(),
                proposal.getProject().getRequestDetail(),
                proposal.getFreelancerProfile().getId(),
                proposal.getFreelancerProfile().getUser().getId(),
                proposal.getFreelancerProfile().getUser().getName(),
                proposal.getStatus(),
                proposal.getProject().getStatus(),
                proposal.getMessage(),
                proposal.getRespondedAt(),
                proposal.getProject().getAcceptedAt(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt()
        );
    }
}
