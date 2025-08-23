package com.ahmadda.presentation.filter.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class RateLimitExceededHandlerTest {

    RateLimitExceededHandler handler;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        handler = new RateLimitExceededHandler(objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void 응답에_429_상태코드와_RetryAfter헤더_그리고_ProblemDetail본문이_포함된다() throws Exception {
        // given
        request.setRequestURI("/api/test");

        // when
        handler.handle(request, response, 42);

        // then
        var body = response.getContentAsString();

        assertSoftly(softly -> {
            softly.assertThat(response.getStatus())
                    .isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
            softly.assertThat(response.getHeader(HttpHeaders.RETRY_AFTER))
                    .isEqualTo("42");
            softly.assertThat(response.getContentType())
                    .isEqualTo("application/problem+json;charset=UTF-8");

            softly.assertThat(body)
                    .contains("Too Many Requests");
            softly.assertThat(body)
                    .contains("요청이 너무 많습니다");
            softly.assertThat(body)
                    .contains("/api/test");
        });
    }
}
