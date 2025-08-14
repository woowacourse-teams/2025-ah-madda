package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMemberWithOptOut;

public record OrganizationMemberWithOptOutResponse(
        Long organizationMemberId,
        String nickname,
        boolean optedOut
) {

    public static OrganizationMemberWithOptOutResponse from(final OrganizationMemberWithOptOut organizationMemberWithOptOut) {
        return new OrganizationMemberWithOptOutResponse(
                organizationMemberWithOptOut.getOrganizationMember()
                        .getId(),
                organizationMemberWithOptOut.getOrganizationMember()
                        .getNickname(),
                organizationMemberWithOptOut.isOptedOut()
        );
    }
}
