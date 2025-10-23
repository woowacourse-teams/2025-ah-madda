package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.ApprovalStatus;
import com.ahmadda.domain.event.GuestWithOptStatus;

public record GuestWithOptOutResponse(
        Long guestId,
        Long organizationMemberId,
        String nickname,
        boolean optedOut,
        ApprovalStatus approvalStatus
) {

    public static GuestWithOptOutResponse from(final GuestWithOptStatus guestWithOptStatus) {
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
                guestWithOptStatus.getGuest()
                        .getApprovalStatus()
        );
    }
}
