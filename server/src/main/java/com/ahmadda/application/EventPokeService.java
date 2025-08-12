package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventPokeReminder;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.presentation.dto.NotifyPokeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventPokeService {

    private final EventPokeReminder eventPokeReminder;
    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public void poke(final Long eventId, final NotifyPokeRequest notifyPokeRequest, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember sender = getOrganizationMember(loginMember, organization);
        OrganizationMember recipient = getOrganizationMember(notifyPokeRequest.receiptOrganizationMemberId());

        eventPokeReminder.sendPoke(sender, recipient, event, LocalDateTime.now());
    }

    private OrganizationMember getOrganizationMember(Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("조직원을 찾는데 실패하였습니다."));
    }

    private OrganizationMember getOrganizationMember(final LoginMember loginMember, final Organization organization) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(
                        loginMember.memberId(),
                        organization.getId()
                )
                .orElseThrow(() -> new NotFoundException("조직원을 찾는데 실패하였습니다."));
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }
}
