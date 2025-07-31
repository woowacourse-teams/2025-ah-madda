package com.ahmadda.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventNotification {

    private final NotificationMailer notificationMailer;

    public void sendEmails(final Event event, final List<OrganizationMember> recipients, final String content) {
        String subject = createSubject(event);
        Map<String, Object> model = createModel(event, content);

        recipients.forEach(recipient ->
                notificationMailer.sendEmail(
                        recipient.getMember()
                                .getEmail(),
                        subject,
                        model
                )
        );
    }

    private String createSubject(final Event event) {
        return "[%s] %s님의 이벤트 안내: %s".formatted(
                event.getOrganization()
                        .getName(),
                event.getOrganizer()
                        .getNickname(),
                event.getTitle()
        );
    }

    private Map<String, Object> createModel(final Event event, final String content) {
        Map<String, Object> model = new HashMap<>();
        model.put(
                "organizationName",
                event.getOrganization()
                        .getName()
        );
        model.put("content", content);
        model.put("title", event.getTitle());
        model.put("organizerNickname", event.getOrganizerNickname());
        model.put("place", event.getPlace());
        model.put("registrationStart", event.getRegistrationStart());
        model.put("registrationEnd", event.getRegistrationEnd());
        model.put("eventStart", event.getEventStart());
        model.put("eventEnd", event.getEventEnd());
        model.put("eventId", event.getId());

        return model;
    }
}
