package com.ieum.ansimdonghaeng.domain.auth.repository;

import com.ieum.ansimdonghaeng.domain.auth.entity.RefreshToken;
import com.ieum.ansimdonghaeng.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    void deleteByUser(User user);
}
