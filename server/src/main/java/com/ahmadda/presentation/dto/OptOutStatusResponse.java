package com.ahmadda.presentation.dto;

import com.ahmadda.domain.OrganizationMemberWithOptStatus;

public record OptOutStatusResponse(boolean optedOut) {

    public static OptOutStatusResponse from(final OrganizationMemberWithOptStatus organizationMemberWithOptStatus) {
        return new OptOutStatusResponse(organizationMemberWithOptStatus.isOptedOut());
    }
}
