package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Template;

public record TemplateTitleResponse(
        Long templateId,
        String title
) {

    public static TemplateTitleResponse from(final Template template) {
        return new TemplateTitleResponse(
                template.getId(),
                template.getTitle()
        );
    }
}
