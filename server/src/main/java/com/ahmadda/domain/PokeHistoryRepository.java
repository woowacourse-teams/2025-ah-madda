package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    //TODO 성능 개선 필요
    int countPokeHistoryByEventAndSenderAndRecipientAndSentAtAfter(
            final Event event,
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final LocalDateTime sentAt
    );
}
