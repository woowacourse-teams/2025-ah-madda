package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.Poke;
import com.ahmadda.presentation.dto.PokeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PokeService {

    private final Poke poke;
    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public void poke(final Long eventId, final PokeRequest notifyPokeRequest, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember sender = getOrganizationMember(loginMember, organization);
        OrganizationMember recipient = getOrganizationMember(notifyPokeRequest.receiptOrganizationMemberId());

        poke.doPoke(sender, recipient, event, LocalDateTime.now());
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    private OrganizationMember getOrganizationMember(final LoginMember loginMember, final Organization organization) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(
                        organization.getId(),
                        loginMember.memberId()
                )
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }
}
