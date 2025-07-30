package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Guest;
import com.ahmadda.domain.OrganizationMember;

public record GuestResponse(
        Long guestId,
        Long organizationMemberId,
        String nickname
) {

    public static GuestResponse from(final Guest guest) {
        OrganizationMember organizationMember = guest.getOrganizationMember();

        return new GuestResponse(
                guest.getId(),
                organizationMember.getId(),
                organizationMember.getNickname()
        );
    }
}
