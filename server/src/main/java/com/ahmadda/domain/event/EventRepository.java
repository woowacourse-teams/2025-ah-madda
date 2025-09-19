package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventOperationPeriodRegistrationEventPeriodEndBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );

    List<Event> findAllByOrganizationAndEventOperationPeriodEventPeriodEndBefore(
            final Organization organization,
            final LocalDateTime now
    );

    List<Event> findAllByEventOperationPeriodEventPeriodStartBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );
}
