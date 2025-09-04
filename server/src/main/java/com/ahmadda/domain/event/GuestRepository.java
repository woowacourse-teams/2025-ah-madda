package com.ahmadda.domain.event;

import com.ahmadda.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    void deleteByEventAndOrganizationMember(final Event event, final OrganizationMember organizationMember);
}
