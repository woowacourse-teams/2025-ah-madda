package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventGuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    // TODO. 추후 주최자에 대한 인가 처리 필요
    public List<Guest> getGuests(final Long eventId) {
        final Event event = getEvent(eventId);

        return event.getGuests();
    }

    // TODO. 추후 주최자에 대한 인가 처리 필요
    public List<OrganizationMember> getNonGuestOrganizationMembers(final Long eventId) {
        final Event event = getEvent(eventId);
        final Organization organization = event.getOrganization();
        final List<OrganizationMember> allMembers = organization.getOrganizationMembers();

        return event.getNonGuestOrganizationMembers(allMembers);
    }

    public void participantEvent(
            final Long eventId,
            final Long organizationMemberId,
            final LocalDateTime currentDateTime
    ) {
        Event event = getEvent(eventId);
        OrganizationMember organizationMember = getOrganizationMember(organizationMemberId);

        Guest guest = Guest.create(event, organizationMember, currentDateTime);

        guestRepository.save(guest);
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }
}
