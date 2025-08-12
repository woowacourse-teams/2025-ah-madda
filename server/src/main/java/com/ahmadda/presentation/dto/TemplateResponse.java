package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Template;

public record TemplateResponse(
        Long templateId,
        String description
) {

    public static TemplateResponse from(final Template template) {
        return new TemplateResponse(
                template.getId(),
                template.getDescription()
        );
    }
}
