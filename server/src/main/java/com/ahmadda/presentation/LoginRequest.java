package com.ahmadda.presentation;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull String code
) {

}
