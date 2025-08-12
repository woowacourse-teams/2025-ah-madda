package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventTemplate;

public record TemplateResponse(
        Long templateId,
        String description
) {

    public static TemplateResponse from(final EventTemplate eventTemplate) {
        return new TemplateResponse(
                eventTemplate.getId(),
                eventTemplate.getDescription()
        );
    }
}
