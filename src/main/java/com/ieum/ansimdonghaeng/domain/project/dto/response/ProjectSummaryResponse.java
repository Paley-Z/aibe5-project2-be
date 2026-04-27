package com.ieum.ansimdonghaeng.domain.project.dto.response;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import com.ieum.ansimdonghaeng.domain.project.repository.ProjectSummaryView;
import java.time.LocalDateTime;

// 프로젝트 목록 카드에 필요한 요약 정보를 담는다.
public record ProjectSummaryResponse(
        Long projectId,
        String title,
        String ownerName,
        String projectTypeCode,
        String serviceRegionCode,
        LocalDateTime requestedStartAt,
        LocalDateTime requestedEndAt,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // 엔티티를 목록 요약 DTO로 변환한다.
    public static ProjectSummaryResponse from(ProjectSummaryView project) {
        return new ProjectSummaryResponse(
                project.projectId(),
                project.title(),
                project.ownerName(),
                project.projectTypeCode(),
                project.serviceRegionCode(),
                project.requestedStartAt(),
                project.requestedEndAt(),
                project.status(),
                project.createdAt(),
                project.updatedAt()
        );
    }
}
