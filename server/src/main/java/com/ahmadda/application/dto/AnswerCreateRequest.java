package com.ahmadda.application.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record AnswerCreateRequest(
        @NotNull
        Long questionId,
        @Nullable
        String answerText
) {

}
