package com.ahmadda.domain.notification;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOrganizer;

public record EventEmailPayload(
        Subject subject,
        Body body
) {

    private static final long MAX_PRESENT_NICKNAME = 3L;

    public static EventEmailPayload of(final Event event, final String content) {
        String organizerNicknames = createOrganizerNicknames(event);

        Subject subject = new Subject(
                event.getOrganization()
                        .getName(),
                event.getTitle()
        );

        Body body = new Body(
                content,
                event.getOrganization()
                        .getName(),
                event.getTitle(),
                organizerNicknames,
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

    private static String createOrganizerNicknames(final Event event) {
        boolean isTooLongOwners = event.getEventOrganizers()
                .size() > MAX_PRESENT_NICKNAME;

        String organizerNicknames = event.getEventOrganizers()
                .stream()
                .map(EventOrganizer::getNickname)
                .limit(MAX_PRESENT_NICKNAME)
                .collect(Collectors.joining(","));
        if (isTooLongOwners) {
            organizerNicknames += " 등";
        }

        return organizerNicknames;
    }
}
