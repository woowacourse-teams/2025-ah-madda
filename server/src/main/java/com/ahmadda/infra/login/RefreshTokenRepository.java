package com.ahmadda.infra.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);
}
