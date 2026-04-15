package com.ieum.ansimdonghaeng.domain.proposal.dto.response;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.proposal.entity.Proposal;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import java.time.LocalDateTime;

public record ProposalSummaryResponse(
        Long proposalId,
        Long projectId,
        String projectTitle,
        Long ownerUserId,
        ProposalStatus proposalStatus,
        ProjectStatus projectStatus,
        String message,
        LocalDateTime respondedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // 프리랜서 목록 응답에서는 제안과 프로젝트의 핵심 상태만 요약해서 내려준다.
    public static ProposalSummaryResponse from(Proposal proposal) {
        return new ProposalSummaryResponse(
                proposal.getId(),
                proposal.getProject().getId(),
                proposal.getProject().getTitle(),
                proposal.getProject().getOwnerUserId(),
                proposal.getStatus(),
                proposal.getProject().getStatus(),
                proposal.getMessage(),
                proposal.getRespondedAt(),
                proposal.getCreatedAt(),
                proposal.getUpdatedAt()
        );
    }
}
