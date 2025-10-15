package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SelectedOrganizationMembersNotificationRequest(
        @NotEmpty
        List<Long> organizationMemberIds,
        @NotBlank
        String content
) {

}
