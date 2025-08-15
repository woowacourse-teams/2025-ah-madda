package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SelectedOrganizationMembersNotificationRequest(
        // TODO. 추후 요청 Body 크기 자체를 제한하도록 변경
        @NotEmpty
        List<Long> organizationMemberIds,
        @NotBlank
        @Size(max = 20)
        String content
) {

}
