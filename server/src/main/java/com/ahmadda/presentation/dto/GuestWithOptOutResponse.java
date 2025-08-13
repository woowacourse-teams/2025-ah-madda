package com.ahmadda.presentation.dto;

import com.ahmadda.domain.GuestWithOptOut;

public record GuestWithOptOutResponse(
        Long guestId,
        Long organizationMemberId,
        String nickname,
        boolean optedOut
) {

    public static GuestWithOptOutResponse from(final GuestWithOptOut guestWithOptOut) {
        return new GuestWithOptOutResponse(
                guestWithOptOut.getGuest()
                        .getId(),
                guestWithOptOut.getGuest()
                        .getOrganizationMember()
                        .getId(),
                guestWithOptOut.getGuest()
                        .getOrganizationMember()
                        .getNickname(),
                guestWithOptOut.isOptedOut()
        );
    }
}
