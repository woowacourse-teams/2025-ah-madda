package com.ahmadda.presentation.filter.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RateLimitExceededHandler {

    private final ObjectMapper objectMapper;

    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long retryAfterSeconds
    ) throws IOException {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setDetail("요청이 너무 많습니다. " + retryAfterSeconds + "초 후 다시 시도해 주세요.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(objectMapper.writeValueAsString(problemDetail));
    }
}
