package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OpenProfileUpdateRequest(
        @NotBlank
        String nickname,
        @NotNull
        Long groupId
) {

}
