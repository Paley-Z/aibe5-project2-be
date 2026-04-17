package com.ieum.ansimdonghaeng.domain.report.dto.response;

import com.ieum.ansimdonghaeng.domain.report.entity.Report;
import java.time.LocalDateTime;

public record ReportCreateResponse(
        Long reportId,
        Long reviewId,
        String reasonType,
        String status,
        LocalDateTime createdAt
) {

    public static ReportCreateResponse from(Report report) {
        return new ReportCreateResponse(
                report.getId(),
                report.getReview().getId(),
                report.getReasonType().name(),
                report.getStatus().name(),
                report.getCreatedAt()
        );
    }
}
