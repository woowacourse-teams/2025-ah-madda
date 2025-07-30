package com.ahmadda.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotification {

    private final NotificationMailer notificationMailer;

    public void sendEmails(final Event event, final List<OrganizationMember> recipients, final String content) {
        String subject = generateSubject(event);

        recipients.forEach(recipient ->
                notificationMailer.sendEmail(
                        recipient.getMember()
                                .getEmail(),
                        subject,
                        // TODO. 템플릿을 이용하여 content 생성하는 로직으로 변경 필요
                        content
                )
        );
    }

    private String generateSubject(final Event event) {
        return "[%s] %s님의 이벤트 안내: %s".formatted(
                event.getOrganization()
                        .getName(),
                event.getOrganizer()
                        .getNickname(),
                event.getTitle()
        );
    }
}
