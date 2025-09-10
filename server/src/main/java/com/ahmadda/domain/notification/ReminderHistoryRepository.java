package com.ahmadda.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderHistoryRepository extends JpaRepository<ReminderHistory, Long> {

    List<ReminderHistory> findByEventId(final Long eventId);

    List<ReminderHistory> findTop10ByEventIdAndCreatedAtAfterOrderByCreatedAtDesc(
            final Long organizerId,
            final LocalDateTime after
    );
}
