package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.EventTemplate;

public record EventTemplateResponse(
        Long templateId,
        String description
) {

    public static EventTemplateResponse from(final EventTemplate eventTemplate) {
        return new EventTemplateResponse(
                eventTemplate.getId(),
                eventTemplate.getDescription()
        );
    }
}
