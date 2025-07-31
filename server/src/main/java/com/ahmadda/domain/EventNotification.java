package com.ahmadda.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotification {

    private final NotificationMailer notificationMailer;

    public void sendEmails(final List<OrganizationMember> recipients, final Email email) {
        recipients.forEach(recipient ->
                notificationMailer.sendEmail(
                        recipient.getMember()
                                .getEmail(),
                        email
                )
        );
    }
}
