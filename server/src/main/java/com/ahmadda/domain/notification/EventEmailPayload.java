package com.ahmadda.domain.notification;

import com.ahmadda.domain.event.Event;

import java.time.LocalDateTime;

public record EventEmailPayload(
        Subject subject,
        Body body
) {

    public static EventEmailPayload of(final Event event, final String content) {
        Subject subject = new Subject(
                event.getOrganization()
                        .getName(),
                event.getOrganizer()
                        .getNickname(),
                event.getTitle()
        );

        Body body = new Body(
                content,
                event.getOrganization()
                        .getName(),
                event.getTitle(),
                event.getOrganizer()
                        .getNickname(),
                event.getPlace(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getEventStart(),
                event.getEventEnd(),
                event.getOrganization()
                        .getId(),
                event.getId()
        );

        return new EventEmailPayload(subject, body);
    }

    public record Subject(
            String organizationName,
            String organizerNickname,
            String eventTitle
    ) {

    }

    public record Body(
            String content,
            String organizationName,
            String title,
            String organizerNickname,
            String place,
            LocalDateTime registrationStart,
            LocalDateTime registrationEnd,
            LocalDateTime eventStart,
            LocalDateTime eventEnd,
            Long organizationId,
            Long eventId
    ) {

    }
}
