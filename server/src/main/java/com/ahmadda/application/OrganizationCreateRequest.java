package com.ahmadda.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationCreateRequest(@NotBlank @NotNull String name,
                                        @NotBlank @NotNull String description,
                                        @NotNull String imageUrl) {

}
