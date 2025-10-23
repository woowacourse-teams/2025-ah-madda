package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationParticipateRequest(
        @NotBlank
        String inviteCode
) {

}
