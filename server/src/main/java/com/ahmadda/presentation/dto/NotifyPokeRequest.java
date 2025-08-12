package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record NotifyPokeRequest(
        @NotNull
        Long receiptOrganizationMemberId
) {

}
