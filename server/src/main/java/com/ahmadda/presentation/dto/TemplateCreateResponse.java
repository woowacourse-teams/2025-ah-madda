package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Template;

public record TemplateCreateResponse(
        Long templateId,
        String title,
        String description
) {

    public static TemplateCreateResponse from(final Template template) {
        return new TemplateCreateResponse(
                template.getId(),
                template.getTitle(),
                template.getDescription()
        );
    }
}
