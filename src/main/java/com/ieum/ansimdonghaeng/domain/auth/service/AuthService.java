package com.ieum.ansimdonghaeng.domain.auth.service;

import com.ieum.ansimdonghaeng.common.exception.CustomException;
import com.ieum.ansimdonghaeng.common.exception.ErrorCode;
import com.ieum.ansimdonghaeng.common.jwt.JwtTokenProvider;
import com.ieum.ansimdonghaeng.domain.auth.dto.request.AuthLoginRequest;
import com.ieum.ansimdonghaeng.domain.auth.dto.request.AuthRefreshRequest;
import com.ieum.ansimdonghaeng.domain.auth.dto.request.AuthSignupRequest;
import com.ieum.ansimdonghaeng.domain.auth.dto.request.KakaoOAuthLoginRequest;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.AuthSignupResponse;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.AuthTokenResponse;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.AuthUserResponse;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.KakaoUserInfo;
import com.ieum.ansimdonghaeng.domain.auth.entity.RefreshToken;
import com.ieum.ansimdonghaeng.domain.auth.oauth.KakaoOAuthClient;
import com.ieum.ansimdonghaeng.domain.auth.repository.RefreshTokenRepository;
import com.ieum.ansimdonghaeng.domain.user.entity.AuthProvider;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import com.ieum.ansimdonghaeng.domain.user.entity.UserRole;
import com.ieum.ansimdonghaeng.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuthClient kakaoOAuthClient;

    @Transactional
    public AuthTokenResponse issueToken(AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.username())
                .orElseThrow(() -> new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.getMessage()));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        return issueTokensForUser(user);
    }

    @Transactional
    public AuthTokenResponse refresh(AuthRefreshRequest request) {
        String refreshTokenValue = request.refreshToken();

        if (!jwtTokenProvider.validateToken(refreshTokenValue) || !"refresh".equals(jwtTokenProvider.getTokenType(refreshTokenValue))) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenProvider.getUsername(refreshTokenValue);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        RefreshToken savedRefreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!savedRefreshToken.getTokenValue().equals(refreshTokenValue) || savedRefreshToken.isExpired(LocalDateTime.now())) {
            refreshTokenRepository.delete(savedRefreshToken);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return issueTokensForUser(user);
    }

    @Transactional
    public AuthSignupResponse signup(AuthSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .phone(request.phone())
                .intro(request.intro())
                .roleCode(UserRole.USER.getCode())
                .activeYn(true)
                .build();

        User savedUser = userRepository.save(user);
        return new AuthSignupResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getRole().getCode());
    }

    @Transactional
    public AuthTokenResponse kakaoLogin(KakaoOAuthLoginRequest request) {
        KakaoUserInfo kakaoUserInfo = kakaoOAuthClient.getUserInfo(request.accessToken());

        User user = userRepository.findByProviderCodeAndProviderUserId(AuthProvider.KAKAO.getCode(), kakaoUserInfo.providerId())
                .orElseGet(() -> createKakaoUser(kakaoUserInfo));

        if (Boolean.FALSE.equals(user.getActiveYn())) {
            throw new CustomException(ErrorCode.USER_INACTIVE);
        }

        return issueTokensForUser(user);
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "User was not found."));
        refreshTokenRepository.deleteByUser(user);
    }

    private AuthTokenResponse issueTokensForUser(User user) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getCode()));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), authorities);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail(), authorities);
        LocalDateTime refreshExpiresAt = LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationSeconds());

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        refreshToken -> refreshToken.rotate(refreshTokenValue, refreshExpiresAt),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .tokenValue(refreshTokenValue)
                                .expiresAt(refreshExpiresAt)
                                .build())
                );

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                refreshTokenValue,
                jwtTokenProvider.getRefreshTokenExpirationSeconds(),
                new AuthUserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().getCode())
        );
    }

    private User createKakaoUser(KakaoUserInfo kakaoUserInfo) {
        userRepository.findByEmail(kakaoUserInfo.email())
                .ifPresent(user -> {
                    throw new CustomException(ErrorCode.OAUTH_ACCOUNT_CONFLICT);
                });

        User user = User.builder()
                .email(kakaoUserInfo.email())
                .passwordHash(passwordEncoder.encode(AuthProvider.KAKAO.getCode() + ":" + kakaoUserInfo.providerId()))
                .name(kakaoUserInfo.nickname())
                .roleCode(UserRole.USER.getCode())
                .activeYn(true)
                .providerCode(AuthProvider.KAKAO.getCode())
                .providerUserId(kakaoUserInfo.providerId())
                .build();

        return userRepository.save(user);
    }
}
