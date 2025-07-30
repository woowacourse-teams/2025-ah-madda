package com.ahmadda.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotification {

    private final NotificationMailer notificationMailer;

    public void sendEmails(final Event event, final List<OrganizationMember> recipients, final String content) {
        String subject = generateSubject(event);
        String text = generateText(event, content);

        recipients.forEach(recipient ->
                notificationMailer.sendEmail(
                        recipient.getMember()
                                .getEmail(),
                        subject,
                        text
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

    // TODO. 템플릿을 이용하여 content 생성하는 로직으로 변경 필요
    private String generateText(final Event event, final String content) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

        return """
                안녕하세요. %s 입니다.
                
                %s
                
                ────────────────────────────────
                [이벤트 정보]
                • 제목: %s
                • 주최자: %s
                • 장소: %s
                • 모집기간: %s ~ %s
                • 진행기간: %s ~ %s
                ────────────────────────────────
                
                감사합니다.
                """.formatted(
                event.getOrganization()
                        .getName(),
                content,
                event.getTitle(),
                event.getOrganizerNickname(),
                event.getPlace(),
                event.getRegistrationStart()
                        .format(formatter),
                event.getRegistrationEnd()
                        .format(formatter),
                event.getEventStart()
                        .format(formatter),
                event.getEventEnd()
                        .format(formatter)
        );
    }
}
