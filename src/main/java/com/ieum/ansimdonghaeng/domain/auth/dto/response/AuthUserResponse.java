package com.ieum.ansimdonghaeng.domain.auth.dto.response;

public record AuthUserResponse(
        Long userId,
        String email,
        String name,
        String roleCode
) {
}
