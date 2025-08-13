package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventNotificationOptOutRepository extends JpaRepository<EventNotificationOptOut, Long> {

    boolean existsByEventAndOrganizationMember(final Event event, final OrganizationMember organizationMember);

    Optional<EventNotificationOptOut> findByEventAndOrganizationMember(
            final Event event,
            final OrganizationMember organizationMember
    );
}
