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

    @Query("""
            SELECT e
            FROM Event e
            WHERE e.organization = :organization
              AND (
                    e.eventOperationPeriod.eventPeriod.end < :lastEnd
                    OR (
                        e.eventOperationPeriod.eventPeriod.end = :lastEnd
                        AND e.id < :lastId
                    )
                  )
            ORDER BY e.eventOperationPeriod.eventPeriod.end DESC, e.id DESC
            """)
    List<Event> findPastEventsByOrganizationWithCursor(
            @Param("organization") final Organization organization,
            @Param("lastEnd") final LocalDateTime lastEnd,
            @Param("lastId") final Long lastId,
            final Pageable pageable
    );
}
