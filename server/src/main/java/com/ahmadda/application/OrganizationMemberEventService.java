package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberEventService {

    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public List<Event> getOwnerEvents(final Long organizationMemberId) {
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        return eventRepository.findAllByOrganizer(organizationMember);
    }

    public List<Event> getParticipantEvents(final Long organizationMemberId) {
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        return organizationMember.getParticipatedEvents();
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직원 정보입니다"));
    }
}
