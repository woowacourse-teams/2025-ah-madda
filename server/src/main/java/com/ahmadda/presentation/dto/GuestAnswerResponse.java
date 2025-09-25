package com.ahmadda.presentation.dto;

import com.ahmadda.domain.event.Answer;
import com.ahmadda.domain.event.Question;

public record GuestAnswerResponse(
        Long questionId,
        String questionText,
        Long answerId,
        String answerText,
        int orderIndex
) {

    public static GuestAnswerResponse from(final Answer answer) {
        Question question = answer.getQuestion();

        return new GuestAnswerResponse(
                question.getId(),
                question.getQuestionText(),
                answer.getId(),
                answer.getAnswerText(),
                question.getOrderIndex()
        );
    }
}
