package com.ieum.ansimdonghaeng.domain.auth.dto.response;

public record KakaoUserInfo(
        String providerId,
        String email,
        String nickname
) {
}
