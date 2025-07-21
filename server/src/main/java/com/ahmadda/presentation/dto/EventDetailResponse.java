package com.ahmadda.presentation.dto;

import com.ahmadda.domain.Event;
import com.ahmadda.domain.Question;

import java.time.LocalDateTime;
import java.util.List;

public record EventDetailResponse(
        Long eventId,
        String title,
        String description,
        String place,
        String organizerName,
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

        public static QuestionResponse from(Question question) {
            return new QuestionResponse(
                    question.getId(),
                    question.getQuestionText(),
                    question.isRequired(),
                    question.getOrderIndex()
            );
        }
    }

    public static EventDetailResponse from(Event event) {
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getPlace(),
                event.getOrganizer()
                        .getNickname(),
                event.geEventStart(),
                event.geEventEnd(),
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
