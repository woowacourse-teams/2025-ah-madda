package com.ahmadda.application.dto;

import jakarta.validation.Valid;

import java.util.List;

public record EventParticipateRequest(
        @Valid
        List<AnswerCreateRequest> answers
) {

}
