package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventTemplate;

public record TemplateTitleResponse(
        Long templateId,
        String title
) {

    public static TemplateTitleResponse from(final EventTemplate eventTemplate) {
        return new TemplateTitleResponse(
                eventTemplate.getId(),
                eventTemplate.getTitle()
        );
    }
}
