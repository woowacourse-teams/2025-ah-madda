package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.util.Assert;
import lombok.Getter;

import java.util.List;

@Getter
public class GuestWithOptOut {

    private final Guest guest;
    private final boolean optedOut;

    private GuestWithOptOut(final Guest guest, final boolean optedOut) {
        validateGuest(guest);
        validateEvent(guest.getEvent());
        validateOrganizationMember(guest.getOrganizationMember());
        validateOrganizerCannotOptOut(guest, optedOut);

        this.guest = guest;
        this.optedOut = optedOut;
    }

    public static GuestWithOptOut create(final Guest guest, final boolean optedOut) {
        return new GuestWithOptOut(guest, optedOut);
    }

    public static GuestWithOptOut createWithOptOutStatus(
            final Guest guest,
            final EventNotificationOptOutRepository optOutRepository
    ) {
        boolean optedOut =
                optOutRepository.existsByEventAndOrganizationMember(guest.getEvent(), guest.getOrganizationMember());

        return new GuestWithOptOut(guest, optedOut);
    }

    public static List<OrganizationMember> extractOptInOrganizationMembers(final List<GuestWithOptOut> guestsWithOptOut) {
        return guestsWithOptOut.stream()
                .filter(guestWithOptOut -> !guestWithOptOut.isOptedOut())
                .map(GuestWithOptOut::getOrganizationMember)
                .toList();
    }

    private void validateGuest(final Guest guest) {
        Assert.notNull(guest, "게스트는 null이 될 수 없습니다.");
    }

    private void validateEvent(final Event event) {
        Assert.notNull(event, "게스트는 반드시 이벤트에 속해야 합니다.");
    }

    private void validateOrganizationMember(final OrganizationMember organizationMember) {
        Assert.notNull(organizationMember, "게스트는 반드시 조직원이어야 합니다.");
    }

    private void validateOrganizerCannotOptOut(final Guest guest, final boolean optedOut) {
        if (guest.getEvent()
                .isOrganizer(guest.getOrganizationMember()) && optedOut) {
            throw new BusinessRuleViolatedException("이벤트 주최자는 알림 수신 거부 상태일 수 없습니다.");
        }
    }

    public Event getEvent() {
        return guest.getEvent();
    }

    public OrganizationMember getOrganizationMember() {
        return guest.getOrganizationMember();
    }
}
