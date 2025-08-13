package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMember;

public record OrganizationMemberWithOptOutResponse(
        Long organizationMemberId,
        String nickname,
        boolean optedOut
) {

    public static OrganizationMemberWithOptOutResponse from(
            final OrganizationMember organizationMember,
            final boolean optedOut
    ) {
        return new OrganizationMemberWithOptOutResponse(
                organizationMember.getId(),
                organizationMember.getNickname(),
                optedOut
        );
    }
}
