package com.ahmadda.domain.notification;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    List<PokeHistory> findAllByEventAndSenderAndRecipientAndSentAtAfter(
            final Event event,
            final OrganizationMember sender,
            final OrganizationMember recipient,
            final LocalDateTime findDuplicateStartTime
    );
}
