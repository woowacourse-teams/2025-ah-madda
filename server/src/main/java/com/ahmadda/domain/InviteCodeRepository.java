package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    Optional<InviteCode> findFirstByInviterAndExpiresAtAfter(OrganizationMember inviter, LocalDateTime now);
}
