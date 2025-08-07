package com.ahmadda.application;

import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotificationScheduler {

    // TODO. 추후 5분이라는 시간을 보장하도록 구현
    private static final int REMINDER_MINUTES_BEFORE = 5;

    private final EventRepository eventRepository;
    private final EmailNotifier emailNotifier;
    private final PushNotifier pushNotifier;

    // TODO. 추후 중복 알람을 방지하도록 구현
    @Scheduled(fixedRate = 180_000)
    public void notifyRegistrationClosingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.plusMinutes(REMINDER_MINUTES_BEFORE);

        List<Event> upcomingEvents =
                eventRepository.findAllByEventOperationPeriodRegistrationPeriodEndBetween(now, targetTime);

        upcomingEvents.stream()
                .filter(event -> !event.isFull()).
                forEach(upcomingEvent -> {
                    List<OrganizationMember> recipients = upcomingEvent.getNonGuestOrganizationMembers(
                            upcomingEvent.getOrganization()
                                    .getOrganizationMembers()
                    );
                    String content = "이벤트 신청 마감이 임박했습니다.";

                    sendNotificationToRecipients(recipients, upcomingEvent, content);
                });
    }

    private void sendNotificationToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        sendEmailsToRecipients(recipients, event, content);
        sendPushNotificationsToRecipients(recipients, event, content);
    }

    private void sendEmailsToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        EventEmailPayload eventEmailPayload = EventEmailPayload.of(event, content);

        emailNotifier.sendEmails(recipients, eventEmailPayload);
    }

    private void sendPushNotificationsToRecipients(
            final List<OrganizationMember> recipients,
            final Event event,
            final String content
    ) {
        PushNotificationPayload pushNotificationPayload = PushNotificationPayload.of(event, content);

        pushNotifier.sendPushs(recipients, pushNotificationPayload);
    }
}
