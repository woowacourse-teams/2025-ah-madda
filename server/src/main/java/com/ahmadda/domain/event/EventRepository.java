package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.Organization;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            select e
            from Event e
            where e.organization = :organization
              and (
                    e.eventOperationPeriod.eventPeriod.end < :lastend
                    or (
                        e.eventOperationPeriod.eventPeriod.end = :lastend
                        and e.id < :lastid
                    )
                  )
            order by e.eventOperationPeriod.eventPeriod.end desc, e.id desc
            """)
    List<Event> findPastEventsByOrganizationWithCursor(
            final Organization organization,
            final LocalDateTime lastEnd,
            final Long lastId,
            final Pageable pageable
    );
}
