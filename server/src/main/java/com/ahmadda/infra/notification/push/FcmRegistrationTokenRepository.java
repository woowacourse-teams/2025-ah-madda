package com.ahmadda.infra.notification.push;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FcmRegistrationTokenRepository extends JpaRepository<FcmRegistrationToken, Long> {

    Optional<FcmRegistrationToken> findByRegistrationTokenAndMemberId(
            final String registrationToken,
            final Long memberId
    );

    List<FcmRegistrationToken> findAllByMemberIdIn(final List<Long> memberIds);

    Optional<FcmRegistrationToken> findByMemberId(final Long memberId);

    @Transactional
    void deleteAllByRegistrationTokenIn(final List<String> registrationTokens);

    void deleteByRegistrationToken(final String registrationToken);
}
