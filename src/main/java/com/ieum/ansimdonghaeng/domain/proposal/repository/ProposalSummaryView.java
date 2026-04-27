package com.ieum.ansimdonghaeng.domain.proposal.repository;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.proposal.entity.ProposalStatus;
import java.time.LocalDateTime;

public record ProposalSummaryView(
        Long proposalId,
        Long projectId,
        String projectTitle,
        Long ownerUserId,
        String ownerName,
        ProposalStatus proposalStatus,
        ProjectStatus projectStatus,
        String message,
        LocalDateTime respondedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
