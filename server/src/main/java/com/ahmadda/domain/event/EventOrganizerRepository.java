package com.ahmadda.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {

    List<EventOrganizer> findAllByOrganizationMemberId(final Long organizationMemberId);
}
