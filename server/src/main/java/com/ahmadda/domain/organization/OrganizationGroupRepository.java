package com.ahmadda.domain.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationGroupRepository extends JpaRepository<OrganizationGroup, Long> {

    Optional<OrganizationGroup> findByName(String name);

}
