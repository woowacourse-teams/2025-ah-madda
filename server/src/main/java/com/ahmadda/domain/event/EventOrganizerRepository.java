package com.ahmadda.domain.event;

import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, Long> {

    List<EventOrganizer> findAllByOrganizationMemberId(final Long organizationMemberId);

    Optional<EventOrganizer> findByEventAndOrganizationMember(
            final Event event,
            final OrganizationMember organizationMember
    );

    @Query("""
                select eo
                from EventOrganizer eo
                join eo.organizationMember om
                join om.member
                join fetch eo.event
                where om.member = :member
            """)
    List<EventOrganizer> findAllByMember(final Member member);
}
