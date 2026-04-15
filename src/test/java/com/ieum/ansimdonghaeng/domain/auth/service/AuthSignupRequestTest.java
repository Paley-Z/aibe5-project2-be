package com.ieum.ansimdonghaeng.domain.auth.service;

import com.ieum.ansimdonghaeng.common.exception.CustomException;
import com.ieum.ansimdonghaeng.common.exception.ErrorCode;
import com.ieum.ansimdonghaeng.domain.auth.dto.request.AuthSignupRequest;
import com.ieum.ansimdonghaeng.domain.auth.dto.response.AuthSignupResponse;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import com.ieum.ansimdonghaeng.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceSignupTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("signup stores encoded password and default role")
    void signupSuccess() {
        AuthSignupRequest request = new AuthSignupRequest(
                "signup@test.com",
                "1234",
                "signup-user",
                "010-1234-5678",
                "intro"
        );

        AuthSignupResponse response = authService.signup(request);

        Optional<User> savedUser = userRepository.findByEmail("signup@test.com");

        assertThat(response.email()).isEqualTo("signup@test.com");
        assertThat(response.roleCode()).isEqualTo("ROLE_USER");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo("signup-user");
        assertThat(savedUser.get().getPhone()).isEqualTo("010-1234-5678");
        assertThat(savedUser.get().getIntro()).isEqualTo("intro");
        assertThat(savedUser.get().getRoleCode()).isEqualTo("ROLE_USER");
        assertThat(savedUser.get().getActiveYn()).isTrue();
        assertThat(savedUser.get().getPasswordHash()).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", savedUser.get().getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("signup rejects duplicate email")
    void signupDuplicateEmail() {
        userRepository.save(User.builder()
                .email("duplicate@test.com")
                .passwordHash(passwordEncoder.encode("1234"))
                .name("existing")
                .phone("010-0000-0000")
                .intro("existing-user")
                .roleCode("ROLE_USER")
                .activeYn(true)
                .build());

        AuthSignupRequest request = new AuthSignupRequest(
                "duplicate@test.com",
                "5678",
                "another",
                "010-9999-9999",
                "other"
        );

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }
}
