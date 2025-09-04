package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberEventService {

    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public List<Event> getOwnerEvents(final Long organizationId, final LoginMember loginMember) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        return eventRepository.findAllByOrganizer(organizationMember);
    }

    public List<Event> getParticipantEvents(final Long organizationId, final LoginMember loginMember) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        return organizationMember.getParticipatedEvents();
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final LoginMember loginMember) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직원 정보입니다."));
    }
}
