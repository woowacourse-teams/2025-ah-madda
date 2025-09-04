package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.EventTemplate;

public record EventTemplateTitleResponse(
        Long templateId,
        String title
) {

    public static EventTemplateTitleResponse from(final EventTemplate eventTemplate) {
        return new EventTemplateTitleResponse(
                eventTemplate.getId(),
                eventTemplate.getTitle()
        );
    }
}
