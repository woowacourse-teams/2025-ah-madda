package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderHistoryRepository extends JpaRepository<ReminderHistory, Long> {

    List<ReminderHistory> findByEventId(final Long eventId);
}
