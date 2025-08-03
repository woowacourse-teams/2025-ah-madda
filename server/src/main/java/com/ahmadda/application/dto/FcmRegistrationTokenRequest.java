package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmRegistrationTokenRequest(
        @NotBlank
        String registrationToken
) {

}
