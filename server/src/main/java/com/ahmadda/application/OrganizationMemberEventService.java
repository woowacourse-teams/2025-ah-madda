package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOwnerOrganizationMember;
import com.ahmadda.domain.event.EventOwnerOrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberEventService {

    private final EventOwnerOrganizationMemberRepository eventOwnerOrganizationMemberRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public List<Event> getOwnerEvents(final Long organizationId, final LoginMember loginMember) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        List<EventOwnerOrganizationMember> eventOwnerOrganizationMembers =
                eventOwnerOrganizationMemberRepository.findAllByOrganizationMemberId(organizationMember.getId());

        return eventOwnerOrganizationMembers.stream()
                .map((EventOwnerOrganizationMember::getEvent))
                .toList();
    }

    public List<Event> getParticipantEvents(final Long organizationId, final LoginMember loginMember) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        return organizationMember.getParticipatedEvents();
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원 정보입니다."));
    }
}
