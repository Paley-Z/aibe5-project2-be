package com.ieum.ansimdonghaeng.domain.user.repository;

import com.ieum.ansimdonghaeng.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderCodeAndProviderUserId(String providerCode, String providerUserId);

    boolean existsByEmail(String email);
}
