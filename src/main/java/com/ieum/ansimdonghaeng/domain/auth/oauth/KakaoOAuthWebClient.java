package com.ieum.ansimdonghaeng.domain.auth.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ieum.ansimdonghaeng.common.exception.CustomException;
import com.ieum.ansimdonghaeng.common.exception.ErrorCode;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.KakaoUserInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class KakaoOAuthWebClient implements KakaoOAuthClient {

    private final WebClient.Builder webClientBuilder;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Override
    public KakaoUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserResponse response = webClientBuilder.build()
                    .get()
                    .uri(kakaoOAuthProperties.getUserInfoUri())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserResponse.class)
                    .block();

            return toUserInfo(response);
        } catch (WebClientResponseException exception) {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR, "Failed to retrieve Kakao user information.");
        }
    }

    private KakaoUserInfo toUserInfo(KakaoUserResponse response) {
        if (response == null || response.id() == null) {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_ERROR, "Kakao user id was not returned.");
        }

        String email = Optional.ofNullable(response.kakaoAccount())
                .map(KakaoAccount::email)
                .orElseThrow(() -> new CustomException(ErrorCode.OAUTH_EMAIL_NOT_PROVIDED));

        String nickname = Optional.ofNullable(response.kakaoAccount())
                .map(KakaoAccount::profile)
                .map(KakaoProfile::nickname)
                .filter(value -> !value.isBlank())
                .orElse(email);

        return new KakaoUserInfo(String.valueOf(response.id()), email, nickname);
    }

    private record KakaoUserResponse(
            Long id,
            @JsonProperty("kakao_account")
            KakaoAccount kakaoAccount
    ) {
    }

    private record KakaoAccount(
            String email,
            KakaoProfile profile
    ) {
    }

    private record KakaoProfile(
            String nickname
    ) {
    }
}
