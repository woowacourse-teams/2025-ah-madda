package com.ahmadda.domain.organization;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByOrganizationAndName(Organization organization, String name);
}
