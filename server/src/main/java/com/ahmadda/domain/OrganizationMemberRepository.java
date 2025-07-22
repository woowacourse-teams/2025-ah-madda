package com.ahmadda.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import java.util.Optional;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {

    @Query("""
                select om
                from OrganizationMember om
                where om.organization.id = :organizationId
                  and om.member.id = :memberId
            """)
    Optional<OrganizationMember> findByOrganizationIdAndMemberId(final Long organizationId, final Long memberId);
}
