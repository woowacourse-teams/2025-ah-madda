package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank
        String content
) {

}
