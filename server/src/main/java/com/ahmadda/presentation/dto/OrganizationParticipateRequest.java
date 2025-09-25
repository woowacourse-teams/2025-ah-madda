package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationParticipateRequest(
        @NotBlank
        String nickname,
        @NotBlank
        String inviteCode,
        @NotNull
        Long groupId
) {

}
