package com.ahmadda.application.dto;

import com.ahmadda.domain.organization.OrganizationMemberRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrganizationMemberRoleUpdateRequest(
        @NotEmpty
        List<Long> organizationMemberIds,
        @NotNull
        OrganizationMemberRole role
) {

}
