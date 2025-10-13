package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.GuestWithOptStatus;
import com.ahmadda.domain.organization.OrganizationGroup;

public record GuestWithOptOutResponse(
        Long guestId,
        Long organizationMemberId,
        String nickname,
        boolean optedOut,
        String groupName,
        Long groupId
) {

    public static GuestWithOptOutResponse from(final GuestWithOptStatus guestWithOptStatus) {
        OrganizationGroup group = guestWithOptStatus.getOrganizationMember()
                .getGroup();
        
        return new GuestWithOptOutResponse(
                guestWithOptStatus.getGuest()
                        .getId(),
                guestWithOptStatus.getGuest()
                        .getOrganizationMember()
                        .getId(),
                guestWithOptStatus.getGuest()
                        .getOrganizationMember()
                        .getNickname(),
                guestWithOptStatus.isOptedOut(),
                group.getName(),
                group.getId()
        );
    }
}
