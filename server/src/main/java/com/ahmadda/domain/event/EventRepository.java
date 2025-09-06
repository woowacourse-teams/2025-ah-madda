package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrganizer(final OrganizationMember organizer);

    List<Event> findAllByEventOperationPeriodRegistrationEventPeriodEndBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );

    List<Event> findAllByEventOperationPeriodEventPeriodEndBefore(final LocalDateTime now);

    List<Event> findAllByEventOperationPeriodEventPeriodStartBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );
}
