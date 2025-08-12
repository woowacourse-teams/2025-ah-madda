package com.ahmadda.presentation.dto;

import com.ahmadda.domain.EventTemplate;

public record EventTemplateCreateResponse(
        Long templateId
) {

    public static EventTemplateCreateResponse from(final EventTemplate eventTemplate) {
        return new EventTemplateCreateResponse(
                eventTemplate.getId()
        );
    }
}
