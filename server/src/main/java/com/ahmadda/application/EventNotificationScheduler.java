package com.ahmadda.application;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.Reminder;
import com.ahmadda.domain.ReminderHistory;
import com.ahmadda.domain.ReminderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventNotificationScheduler {

    // TODO. 추후 5분이라는 시간을 보장하도록 구현
    private static final Duration SCHEDULER_SCAN_WINDOW = Duration.ofMinutes(5);
    private static final Duration START_REMINDER_LEAD_TIME = Duration.ofHours(24);

    private final EventRepository eventRepository;
    private final Reminder reminder;
    private final ReminderHistoryRepository reminderHistoryRepository;

    // TODO. 추후 중복 알람을 방지하도록 구현
    @Scheduled(fixedRate = 180_000)
    @Transactional
    public void notifyRegistrationClosingEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plus(SCHEDULER_SCAN_WINDOW);

        List<Event> upcomingEvents =
                eventRepository.findAllByEventOperationPeriodRegistrationPeriodEndBetween(now, windowEnd);

        upcomingEvents.stream()
                .filter(event -> !event.isFull()).
                forEach(upcomingEvent -> {
                    List<OrganizationMember> recipients = upcomingEvent.getNonGuestOrganizationMembers(
                            upcomingEvent.getOrganization()
                                    .getOrganizationMembers()
                    );
                    String content = "이벤트 신청 마감이 임박했습니다.";

                    sendAndRecordReminder(upcomingEvent, recipients, content);
                });
    }

    // TODO. 추후 중복 알람을 방지하도록 구현
    @Scheduled(fixedRate = 180_000)
    @Transactional
    public void notifyEventStartIn24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plus(START_REMINDER_LEAD_TIME);
        LocalDateTime windowEnd = windowStart.plus(SCHEDULER_SCAN_WINDOW);

        List<Event> eventsStartingSoon =
                eventRepository.findAllByEventOperationPeriodEventPeriodStartBetween(windowStart, windowEnd);

        eventsStartingSoon.forEach(event -> {
            List<OrganizationMember> recipients = event.getGuests()
                    .stream()
                    .map(Guest::getOrganizationMember)
                    .toList();
            String content = "내일 이벤트가 시작됩니다. 준비되셨나요?";

            sendAndRecordReminder(event, recipients, content);
        });
    }

    private void sendAndRecordReminder(
            final Event upcomingEvent,
            final List<OrganizationMember> recipients,
            final String content
    ) {
        ReminderHistory reminderHistory = reminder.remind(recipients, upcomingEvent, content);
        reminderHistoryRepository.save(reminderHistory);
    }
}
