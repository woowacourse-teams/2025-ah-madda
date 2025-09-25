package com.ahmadda.presentation.dto;

import com.ahmadda.domain.organization.OrganizationGroup;

public record OrganizationGroupResponse(
        Long groupId,
        String name
) {

    public static OrganizationGroupResponse from(final OrganizationGroup organizationGroup) {
        return new OrganizationGroupResponse(organizationGroup.getId(), organizationGroup.getName());
    }
}
