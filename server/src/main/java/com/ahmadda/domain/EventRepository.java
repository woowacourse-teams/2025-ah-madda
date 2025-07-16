package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrganization_IdAndEventStartAfter(Long organizerId, LocalDateTime currentDateTime);

    List<Event> findAllByOrganizer_Member_IdAndOrganization_Id(Long memberId, Long organizerId);
}
