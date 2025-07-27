package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull
        String code,
        @NotNull
        String redirectUri
) {

}
