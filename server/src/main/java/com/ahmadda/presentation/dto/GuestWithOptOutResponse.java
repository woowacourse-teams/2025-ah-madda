package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Guest;
import com.ahmadda.domain.OrganizationMember;

public record GuestWithOptOutResponse(
        Long guestId,
        Long organizationMemberId,
        String nickname,
        boolean optedOut
) {

    public static GuestWithOptOutResponse from(final Guest guest, final boolean optedOut) {
        OrganizationMember organizationMember = guest.getOrganizationMember();

        return new GuestWithOptOutResponse(
                guest.getId(),
                organizationMember.getId(),
                organizationMember.getNickname(),
                optedOut
        );
    }
}
