package com.ahmadda.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventOwnerOrganizationMemberRepository extends JpaRepository<EventOwnerOrganizationMember, Long> {

    List<EventOwnerOrganizationMember> findAllByOrganizationMemberId(final Long organizationMemberId);
}
