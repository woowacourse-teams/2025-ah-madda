package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOrganizer;
import com.ahmadda.domain.event.EventOrganizerRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberEventService {

    private final EventOrganizerRepository eventOrganizerRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public List<Event> getOwnerEvents(final Long organizationId, final LoginMember loginMember) {
        OrganizationMember organizationMember = getOrganizationMember(organizationId, loginMember);

        List<EventOrganizer> eventOrganizers =
                eventOrganizerRepository.findAllByOrganizationMemberId(organizationMember.getId());

        return eventOrganizers.stream()
                .map((EventOrganizer::getEvent))
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
