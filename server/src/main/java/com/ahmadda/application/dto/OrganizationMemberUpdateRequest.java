package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationMemberUpdateRequest(
        @NotBlank
        String nickname,
        @NotNull
        Long groupId
) {

}
