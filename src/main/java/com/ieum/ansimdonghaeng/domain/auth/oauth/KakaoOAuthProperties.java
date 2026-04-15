package com.ieum.ansimdonghaeng.domain.auth.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.oauth.kakao")
public class KakaoOAuthProperties {

    @NotBlank
    private String userInfoUri;
}
