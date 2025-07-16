package com.ahmadda.presentation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationCreateRequest(@NotBlank @NotNull @Min(2) @Max(20) String name,
                                        @NotBlank @NotNull @Min(2) @Max(2000) String description,
                                        @NotNull String imageUrl) {

}
