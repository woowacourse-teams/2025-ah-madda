package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotNull;

public record AnswerCreateRequest(
        @NotNull
        Long questionId,
        String answerText
) {

}
