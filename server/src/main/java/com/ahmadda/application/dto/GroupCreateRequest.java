package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record GroupCreateRequest(
        @NotBlank
        String name
) {

}
