package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationMemberService {

    private final OrganizationMemberRepository organizationMemberRepository;

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않은 조직 멤버 정보입니다"));
    }

    public List<Event> getOwnerEvents(final Long organizationMemberId) {
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        return organizationMember.getOrganization()
                .getEvents()
                .stream()
                .filter(event -> event.isOwner(organizationMemberId))
                .toList();
    }

    public List<Event> getParticipantEvents(final Long organizationMemberId) {
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        List<Event> events = organizationMember.getOrganization().getEvents();
        return events.stream()
                .filter(event -> event.getGuests()
                        .stream()
                        .anyMatch(guest -> guest.getParticipant()
                                .isSameMember(organizationMemberId)
                        )
                )
                .toList();
    }
}
