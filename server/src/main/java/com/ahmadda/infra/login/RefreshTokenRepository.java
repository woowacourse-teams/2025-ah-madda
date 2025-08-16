package com.ahmadda.infra.login;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    void deleteByMemberIdAndDeviceId(final Long memberId, final String deviceId);

    Optional<RefreshToken> findByMemberIdAndDeviceId(final Long memberId, final String deviceId);
}
