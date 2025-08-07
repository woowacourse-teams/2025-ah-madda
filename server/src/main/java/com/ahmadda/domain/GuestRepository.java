package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    void deleteByEventAndOrganizationMember(final Event event, final OrganizationMember organizationMember);
}
