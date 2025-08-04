package com.ahmadda.infra.notification.push;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmRegistrationTokenRepository extends JpaRepository<FcmRegistrationToken, Long> {

    List<FcmRegistrationToken> findAllByMemberIdIn(final List<Long> memberIds);

    void deleteAllByRegistrationTokenIn(final List<String> registrationTokens);
}
