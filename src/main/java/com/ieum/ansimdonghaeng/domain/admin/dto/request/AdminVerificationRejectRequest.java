package com.ieum.ansimdonghaeng.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AdminVerificationRejectRequest(
        @JsonAlias("reviewComment")
        @NotBlank(message = "reason is required.")
        String reason
) {
}
