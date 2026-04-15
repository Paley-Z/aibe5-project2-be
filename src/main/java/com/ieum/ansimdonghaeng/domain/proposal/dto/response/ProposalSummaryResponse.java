package com.ieum.ansimdonghaeng.domain.proposal.dto.response;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import com.ieum.ansimdonghaeng.domain.proposal.repository.ProposalSummaryView;
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
    public static ProposalSummaryResponse from(ProposalSummaryView proposal) {
        return new ProposalSummaryResponse(
                proposal.proposalId(),
                proposal.projectId(),
                proposal.projectTitle(),
                proposal.ownerUserId(),
                proposal.proposalStatus(),
                proposal.projectStatus(),
                proposal.message(),
                proposal.respondedAt(),
                proposal.createdAt(),
                proposal.updatedAt()
        );
    }
}
