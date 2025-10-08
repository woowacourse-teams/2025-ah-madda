package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventOperationPeriodRegistrationEventPeriodEndBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );

    List<Event> findAllByEventOperationPeriodEventPeriodStartBetween(
            final LocalDateTime from,
            final LocalDateTime to
    );

    @Query("SELECT e FROM Event e " +
            "WHERE e.organization = :organization " +
            "AND e.eventOperationPeriod.eventPeriod.end < :compareDateTime " +
            "AND e.id < :lastEventId " +
            "ORDER BY e.eventOperationPeriod.eventPeriod.end DESC, e.id DESC")
    List<Event> findPastEventByOrganizationAndWithCursor(
            @Param("organization") final Organization organization,
            @Param("compareDateTime") final LocalDateTime compareDateTime,
            @Param("lastEventId") final Long lastEventId,
            final Pageable pageable
    );
}
