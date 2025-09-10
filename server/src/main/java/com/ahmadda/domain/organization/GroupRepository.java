package com.ahmadda.domain.organization;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<OrganizationGroup, Long> {

    boolean existsByOrganizationAndName(final Organization organization, final String name);
}
