package com.ieum.ansimdonghaeng.domain.user.dto.response;

public record PublicUserProfileResponse(
        Long userId,
        String name,
        String intro,
        String roleCode
) {
}
