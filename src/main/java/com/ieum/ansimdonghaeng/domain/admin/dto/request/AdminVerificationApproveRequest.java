package com.ieum.ansimdonghaeng.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AdminVerificationApproveRequest(
        @JsonAlias("reviewComment")
        String comment
) {
}
