package com.ahmadda.infra.notification.push;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRegistrationTokenRepository extends JpaRepository<FcmRegistrationToken, Long> {

    Optional<FcmRegistrationToken> findByRegistrationTokenAndMemberId(
            final String registrationToken,
            final Long memberId
    );

    List<FcmRegistrationToken> findAllByMemberIdIn(final List<Long> memberIds);

    void deleteAllByRegistrationTokenIn(final List<String> registrationTokens);
}
