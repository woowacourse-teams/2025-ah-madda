package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {

    List<EventOrganizer> findAllByOrganizationMemberId(final Long organizationMemberId);

    Optional<EventOrganizer> findByEventAndOrganizationMember(
            final Event event,
            final OrganizationMember organizationMember
    );
}
