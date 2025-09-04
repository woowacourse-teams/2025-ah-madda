package com.ahmadda.domain.organization;

import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import lombok.Getter;

import java.util.List;

@Getter
public class OrganizationMemberWithOptStatus {

    private final OrganizationMember organizationMember;
    private final boolean optedOut;

    private OrganizationMemberWithOptStatus(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        validateOrganizerCannotOptOut(organizationMember, optedOut, event);

        this.organizationMember = organizationMember;
        this.optedOut = optedOut;
    }

    public static OrganizationMemberWithOptStatus create(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        return new OrganizationMemberWithOptStatus(organizationMember, optedOut, event);
    }

    public static OrganizationMemberWithOptStatus createWithOptOutStatus(
            final OrganizationMember organizationMember,
            final Event event,
            final EventNotificationOptOutRepository optOutRepository
    ) {
        boolean optedOut = optOutRepository.existsByEventAndOrganizationMember(event, organizationMember);

        return new OrganizationMemberWithOptStatus(organizationMember, optedOut, event);
    }

    public static List<OrganizationMember> extractOptInOrganizationMembers(final List<OrganizationMemberWithOptStatus> organizationMembersWithOptOuts) {
        return organizationMembersWithOptOuts.stream()
                .filter(organizationMemberWithOptOut -> !organizationMemberWithOptOut.isOptedOut())
                .map(OrganizationMemberWithOptStatus::getOrganizationMember)
                .toList();
    }

    private void validateOrganizerCannotOptOut(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        if (event.isOrganizer(organizationMember) && optedOut) {
            throw new UnprocessableEntityException("이벤트 주최자는 알림 수신 거부 상태일 수 없습니다.");
        }
    }
}
