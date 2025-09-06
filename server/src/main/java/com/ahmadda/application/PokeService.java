package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.notification.Poke;
import com.ahmadda.domain.notification.PokeHistory;
import com.ahmadda.domain.notification.PokeHistoryRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;
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
    private final EventNotificationOptOutRepository eventNotificationOptOutRepository;
    private final PokeHistoryRepository pokeHistoryRepository;

    @Transactional
    public PokeHistory poke(final Long eventId, final PokeRequest notifyPokeRequest, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        Organization organization = event.getOrganization();
        OrganizationMember sender = getOrganizationMember(loginMember, organization);
        OrganizationMember recipient = getOrganizationMember(notifyPokeRequest.receiptOrganizationMemberId());

        validateRecipientOptInStatus(recipient, event);

        PokeHistory pokeHistory = poke.doPoke(sender, recipient, event, LocalDateTime.now());

        return pokeHistoryRepository.save(pokeHistory);
    }

    private OrganizationMember getOrganizationMember(final Long organizationMemberId) {
        return organizationMemberRepository.findById(organizationMemberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원입니다."));
    }

    private OrganizationMember getOrganizationMember(final LoginMember loginMember, final Organization organization) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(
                        organization.getId(),
                        loginMember.memberId()
                )
                .orElseThrow(() -> new NotFoundException("존재하지 않는 구성원입니다."));
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private void validateRecipientOptInStatus(final OrganizationMember recipient, final Event event) {
        OrganizationMemberWithOptStatus recipientWithOptOut =
                OrganizationMemberWithOptStatus.createWithOptOutStatus(
                        recipient,
                        event,
                        eventNotificationOptOutRepository
                );

        if (recipientWithOptOut.isOptedOut()) {
            throw new UnprocessableEntityException("알림을 받지 않는 구성원입니다.");
        }
    }
}
