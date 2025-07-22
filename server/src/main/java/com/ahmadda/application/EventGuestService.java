package com.ahmadda.application;

import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.presentation.dto.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventGuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final MemberRepository memberRepository;

    public List<Guest> getGuests(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());

        return event.getGuests();
    }

    public List<OrganizationMember> getNonGuestOrganizationMembers(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        validateOrganizer(event, loginMember.memberId());

        Organization organization = event.getOrganization();
        List<OrganizationMember> allMembers = organization.getOrganizationMembers();

        return event.getNonGuestOrganizationMembers(allMembers);
    }

    @Transactional
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

    private void validateOrganizer(final Event event, final Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        if (!event.isOrganizer(member)) {
            throw new AccessDeniedException("이벤트 주최자가 아닙니다.");
        }
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }
}
