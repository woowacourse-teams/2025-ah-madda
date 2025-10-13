package com.ahmadda.presentation.dto;

import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationMemberWithOptStatus;

public record OrganizationMemberWithOptOutResponse(
        Long organizationMemberId,
        String nickname,
        boolean optedOut,
        String groupName,
        Long groupId
) {

    public static OrganizationMemberWithOptOutResponse from(final OrganizationMemberWithOptStatus organizationMemberWithOptStatus) {
        OrganizationGroup group = organizationMemberWithOptStatus.getOrganizationMember()
                .getGroup();

        return new OrganizationMemberWithOptOutResponse(
                organizationMemberWithOptStatus.getOrganizationMember()
                        .getId(),
                organizationMemberWithOptStatus.getOrganizationMember()
                        .getNickname(),
                organizationMemberWithOptStatus.isOptedOut(),
                group.getName(),
                group.getId()
        );
    }
}
