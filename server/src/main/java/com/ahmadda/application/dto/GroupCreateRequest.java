package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotNull;

public record GroupCreateRequest(
        @NotNull
        String name
) {
}
