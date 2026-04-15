package com.ieum.ansimdonghaeng.domain.user.dto.response;

public record AccessPolicyResponse(
        String scope,
        String roleCode,
        String message
) {
}
