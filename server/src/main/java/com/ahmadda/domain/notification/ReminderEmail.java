package com.ahmadda.domain.notification;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.OrganizationMember;

import java.util.List;

public record ReminderEmail(
        List<String> recipientEmails,
        EventEmailPayload payload
) {

    public ReminderEmail {
        recipientEmails = List.copyOf(recipientEmails);
    }

    public static ReminderEmail of(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        EventEmailPayload payload = EventEmailPayload.of(event, content);

        List<String> emails = recipients.stream()
                .map(OrganizationMember::getMember)
                .map(Member::getEmail)
                .toList();

        return new ReminderEmail(emails, payload);
    }

}
