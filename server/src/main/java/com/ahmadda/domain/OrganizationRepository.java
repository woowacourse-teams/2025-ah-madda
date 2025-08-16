package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(final String name);

    @Query("""
                select org
                from OrganizationMember om
                join om.organization org
                where om.member = :member
            """)
    List<Organization> findMemberOrganizations(final Member member);
}
