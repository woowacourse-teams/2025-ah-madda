package com.ahmadda.domain.event;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventStatisticRepository extends JpaRepository<EventStatistic, Long> {

    Optional<EventStatistic> findByEventId(Long eventId);
}
