package com.ahmadda.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record OrganizationCreateRequest(@NotBlank @NotNull @Length(min = 2, max = 20) String name,
                                        @NotBlank @NotNull @Length(min = 2, max = 2000) String description,
                                        @NotNull String imageUrl) {

}
