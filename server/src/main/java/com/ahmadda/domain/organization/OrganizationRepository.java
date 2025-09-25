package com.ahmadda.domain.organization;

import com.ahmadda.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("""
                select org
                from OrganizationMember om
                join om.organization org
                where om.member = :member
            """)
    List<Organization> findMemberOrganizations(final Member member);
}
