package com.ieum.ansimdonghaeng.domain.project.repository;

import com.ieum.ansimdonghaeng.domain.project.entity.ProjectStatus;
import java.time.LocalDateTime;

public record ProjectSummaryView(
        Long projectId,
        String title,
        String projectTypeCode,
        String serviceRegionCode,
        LocalDateTime requestedStartAt,
        LocalDateTime requestedEndAt,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
