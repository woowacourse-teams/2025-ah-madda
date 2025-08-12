package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventTemplate;

public record TemplateCreateResponse(
        Long templateId,
        String title,
        String description
) {

    public static TemplateCreateResponse from(final EventTemplate eventTemplate) {
        return new TemplateCreateResponse(
                eventTemplate.getId(),
                eventTemplate.getTitle(),
                eventTemplate.getDescription()
        );
    }
}
