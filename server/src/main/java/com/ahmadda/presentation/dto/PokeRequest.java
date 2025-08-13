package com.ahmadda.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record PokeRequest(
        @NotNull
        Long receiptOrganizationMemberId
) {

}
