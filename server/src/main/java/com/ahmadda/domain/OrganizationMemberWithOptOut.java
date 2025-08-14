package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import lombok.Getter;

@Getter
public class OrganizationMemberWithOptOut {

    private final OrganizationMember organizationMember;
    private final boolean optedOut;

    private OrganizationMemberWithOptOut(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        validateOrganizationMember(organizationMember);
        validateEvent(event);
        validateOrganizerCannotOptOut(organizationMember, optedOut, event);

        this.organizationMember = organizationMember;
        this.optedOut = optedOut;
    }

    public static OrganizationMemberWithOptOut create(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        return new OrganizationMemberWithOptOut(organizationMember, optedOut, event);
    }

    public static OrganizationMemberWithOptOut createWithOptOutStatus(
            final OrganizationMember organizationMember,
            final Event event,
            final EventNotificationOptOutRepository optOutRepository
    ) {
        boolean optedOut = optOutRepository.existsByEventAndOrganizationMember(event, organizationMember);

        return new OrganizationMemberWithOptOut(organizationMember, optedOut, event);
    }
    
    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "조직원은 null이 될 수 없습니다.");
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "조직원은 반드시 이벤트와 함께 전달되어야 합니다.");
    }

    private void validateOrganizerCannotOptOut(
            final OrganizationMember organizationMember,
            final boolean optedOut,
            final Event event
    ) {
        if (event.isOrganizer(organizationMember) && optedOut) {
            throw new BusinessRuleViolatedException("이벤트 주최자는 알림 수신 거부 상태일 수 없습니다.");
        }
    }
}
