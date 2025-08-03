package com.ahmadda.infra.push;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmPushTokenRepository extends JpaRepository<FcmPushToken, Long> {

    List<FcmPushToken> findAllByMemberIdIn(final List<Long> memberIds);
}
