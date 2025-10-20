package com.ahmadda.domain.organization;

import com.ahmadda.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    @Query("""
                select org
                from OrganizationMember om
                join om.organization org
                where om.member = :member
            """)
    List<Organization> findMemberOrganizations(final Member member);

    @Query("""
            SELECT o
            FROM Organization o
            LEFT JOIN o.events e
                ON e.eventOperationPeriod.eventPeriod.end > :now
            GROUP BY o
            ORDER BY COUNT(e.id) DESC
            """)
    List<Organization> findAllOrderByActiveEventsDesc(@Param("now") LocalDateTime now);
}
