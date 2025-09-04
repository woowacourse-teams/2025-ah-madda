package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.notification.EventNotificationOptOut;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestWithOptStatus;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventNotificationOptOutService {

    private final EventRepository eventRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final EventNotificationOptOutRepository optOutRepository;

    @Transactional
    public EventNotificationOptOut optOut(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        OrganizationMember organizationMember = getOrganizationMember(
                event.getOrganization()
                        .getId(),
                loginMember.memberId()
        );

        if (optOutRepository.existsByEventAndOrganizationMember(event, organizationMember)) {
            throw new BusinessFlowViolatedException("이미 해당 이벤트에 대한 알림 수신 거부가 설정되어 있습니다.");
        }

        EventNotificationOptOut optOut = EventNotificationOptOut.create(organizationMember, event);

        return optOutRepository.save(optOut);
    }

    @Transactional
    public void cancelOptOut(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        OrganizationMember organizationMember = getOrganizationMember(
                event.getOrganization()
                        .getId(),
                loginMember.memberId()
        );

        EventNotificationOptOut optOut =
                optOutRepository.findByEventAndOrganizationMember(event, organizationMember)
                        .orElseThrow(() -> new BusinessFlowViolatedException("수신 거부 설정이 존재하지 않습니다."));

        optOutRepository.delete(optOut);
    }

    public OrganizationMemberWithOptStatus getMemberWithOptStatus(final Long eventId, final LoginMember loginMember) {
        Event event = getEvent(eventId);
        OrganizationMember organizationMember = getOrganizationMember(
                event.getOrganization()
                        .getId(),
                loginMember.memberId()
        );

        return OrganizationMemberWithOptStatus.createWithOptOutStatus(
                organizationMember,
                event,
                optOutRepository
        );
    }

    public List<GuestWithOptStatus> mapGuests(final List<Guest> guests) {
        return guests.stream()
                .map(guest -> {
                    boolean optedOut = optOutRepository.existsByEventAndOrganizationMember(
                            guest.getEvent(),
                            guest.getOrganizationMember()
                    );

                    return GuestWithOptStatus.create(guest, optedOut);
                })
                .toList();
    }

    public List<OrganizationMemberWithOptStatus> mapOrganizationMembers(
            final Long eventId,
            final List<OrganizationMember> members
    ) {
        Event event = getEvent(eventId);

        return members.stream()
                .map(member -> {
                    boolean optedOut = optOutRepository.existsByEventAndOrganizationMember(
                            event,
                            member
                    );

                    return OrganizationMemberWithOptStatus.create(member, optedOut, event);
                })
                .toList();
    }

    private Event getEvent(final Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));
    }

    private OrganizationMember getOrganizationMember(final Long organizationId, final Long memberId) {
        return organizationMemberRepository.findByOrganizationIdAndMemberId(organizationId, memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 조직원입니다."));
    }
}
