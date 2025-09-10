package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOwnerOrganizationMember;
import com.ahmadda.domain.event.Question;

import java.time.LocalDateTime;
import java.util.List;

public record EventDetailResponse(
        Long eventId,
        String title,
        String description,
        String place,
        List<String> organizerNicknames,
        LocalDateTime eventStart,
        LocalDateTime eventEnd,
        LocalDateTime registrationStart,
        LocalDateTime registrationEnd,
        int currentGuestCount,
        int maxCapacity,
        List<QuestionResponse> questions
) {

    record QuestionResponse(
            Long questionId,
            String questionText,
            boolean isRequired,
            int orderIndex
    ) {

        public static QuestionResponse from(final Question question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getQuestionText(),
                    question.isRequired(),
                    question.getOrderIndex()
            );
        }
    }

    public static EventDetailResponse from(final Event event) {
        List<String> organizerNicknames = event.getEventOwnerOrganizationMembers()
                .stream()
                .map(EventOwnerOrganizationMember::getNickname)
                .toList();

        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getPlace(),
                organizerNicknames,
                event.getEventStart(),
                event.getEventEnd(),
                event.getRegistrationStart(),
                event.getRegistrationEnd(),
                event.getGuests()
                        .size(),
                event.getMaxCapacity(),
                event.getQuestions()
                        .stream()
                        .map(QuestionResponse::from)
                        .toList()
        );
    }
}
