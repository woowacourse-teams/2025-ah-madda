package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationMemberRenameRequest(
        @NotBlank
        String nickname
) {

}
