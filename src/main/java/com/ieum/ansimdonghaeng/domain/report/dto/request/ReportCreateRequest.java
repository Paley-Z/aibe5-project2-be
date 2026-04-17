package com.ieum.ansimdonghaeng.domain.report.dto.request;

import com.ieum.ansimdonghaeng.domain.report.entity.ReportReasonType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateRequest(
        @NotNull(message = "reasonType is required")
        ReportReasonType reasonType,

        @Size(max = 2000, message = "reasonDetail must be 2000 characters or fewer")
        String reasonDetail
) {
}
