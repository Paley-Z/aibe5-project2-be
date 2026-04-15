package com.ieum.ansimdonghaeng.domain.user.dto.response;

public record UserProfileResponse(
        Long userId,
        String email,
        String name,
        String phone,
        String intro,
        String roleCode,
        boolean active
) {
}
