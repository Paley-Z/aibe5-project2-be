package com.ieum.ansimdonghaeng.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record KakaoOAuthLoginRequest(
        @NotBlank(message = "accessToken is required")
        String accessToken
) {
}
