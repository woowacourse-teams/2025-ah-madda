package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventTemplate;

public record EventTemplateCreateResponse(
        Long templateId,
        String title,
        String description
) {

    public static EventTemplateCreateResponse from(final EventTemplate eventTemplate) {
        return new EventTemplateCreateResponse(
                eventTemplate.getId(),
                eventTemplate.getTitle(),
                eventTemplate.getDescription()
        );
    }
}
