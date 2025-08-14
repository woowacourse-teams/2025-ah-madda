package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMemberWithOptStatus;

public record OrganizationMemberWithOptOutResponse(
        Long organizationMemberId,
        String nickname,
        boolean optedOut
) {

    public static OrganizationMemberWithOptOutResponse from(final OrganizationMemberWithOptStatus organizationMemberWithOptStatus) {
        return new OrganizationMemberWithOptOutResponse(
                organizationMemberWithOptStatus.getOrganizationMember()
                        .getId(),
                organizationMemberWithOptStatus.getOrganizationMember()
                        .getNickname(),
                organizationMemberWithOptStatus.isOptedOut()
        );
    }
}
