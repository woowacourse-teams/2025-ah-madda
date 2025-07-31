package com.ahmadda.domain;

public record Email(
        Subject subject,
        Body body
) {

    public static Email of(final Event event, final String content) {
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
                event.getOrganizerNickname(),
                event.getPlace(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getEventStart(),
                event.getEventEnd(),
                event.getId()
        );

        return new Email(subject, body);
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
            Object registrationStart,
            Object registrationEnd,
            Object eventStart,
            Object eventEnd,
            Long eventId
    ) {

    }
}
