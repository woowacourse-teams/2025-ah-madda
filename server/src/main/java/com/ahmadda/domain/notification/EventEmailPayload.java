package com.ahmadda.domain.notification;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOrganizer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public String renderSubject() {
        return "[아맞다] %s의 이벤트 안내: %s"
                .formatted(subject.organizationName(), subject.eventTitle());
    }

    public String renderBody(final TemplateEngine templateEngine, final String redirectUrlPrefix) {
        Context context = new Context();
        context.setVariables(createModel(redirectUrlPrefix));

        return templateEngine.process("mail/event-notification", context);
    }

    private Map<String, Object> createModel(final String redirectUrlPrefix) {
        Map<String, Object> model = new HashMap<>();
        model.put("organizationName", body.organizationName());
        model.put("content", body.content());
        model.put("title", body.title());
        model.put("organizerNickname", body.organizerNickname());
        model.put("place", body.place());
        model.put("registrationStart", body.registrationStart());
        model.put("registrationEnd", body.registrationEnd());
        model.put("eventStart", body.eventStart());
        model.put("eventEnd", body.eventEnd());
        model.put("redirectUrl", redirectUrlPrefix + body.organizationId() + "/event/" + body.eventId());

        return model;
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
