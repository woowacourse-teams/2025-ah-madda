package com.ahmadda.presentation.dto;

import com.ahmadda.domain.ReminderHistory;

import java.time.LocalDateTime;

public record ReminderHistorySummaryResponse(
        int recipientCount,
        String content,
        LocalDateTime sentAt
) {

    public static ReminderHistorySummaryResponse from(final ReminderHistory reminderHistory) {
        return new ReminderHistorySummaryResponse(
                reminderHistory.getRecipients()
                        .size(),
                reminderHistory.getContent(),
                reminderHistory.getSentAt()
        );
    }
}
