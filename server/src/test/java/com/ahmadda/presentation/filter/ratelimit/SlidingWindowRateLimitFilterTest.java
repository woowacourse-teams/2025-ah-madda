package com.ahmadda.presentation.filter.ratelimit;

import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import com.ahmadda.presentation.header.HeaderProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SlidingWindowRateLimitFilterTest {

    SlidingWindowRateLimitFilter filter;
    JwtProperties jwtProperties;
    JwtProvider jwtProvider;
    HeaderProvider headerProvider;
    RateLimitExceededHandler rateLimitExceededHandler;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    FilterChain chain;

    @BeforeEach
    void setUp() {
        jwtProvider = mock(JwtProvider.class);
        headerProvider = mock(HeaderProvider.class);
        rateLimitExceededHandler = spy(new RateLimitExceededHandler(new ObjectMapper()));
        jwtProperties = mock(JwtProperties.class);

        filter = new SlidingWindowRateLimitFilter(headerProvider, jwtProperties, jwtProvider, rateLimitExceededHandler);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = mock(FilterChain.class);
    }

    @Test
    void 비회원요청은_제한없이_통과된다() throws Exception {
        // given
        request.removeHeader(HttpHeaders.AUTHORIZATION);

        // when
        filter.doFilterInternal(request, response, chain);

        // then
        verify(chain).doFilter(request, response);
        verifyNoInteractions(rateLimitExceededHandler);
    }

    @Test
    void 요청횟수_초과시_핸들러가_호출된다() throws Exception {
        // given
        var memberId = 1L;
        var token = "token";
        var bearerToken = "Bearer " + token;

        request.addHeader(HttpHeaders.AUTHORIZATION, bearerToken);
        request.setRequestURI("/api/test");

        var payload = mock(JwtMemberPayload.class);
        when(payload.getMemberId()).thenReturn(memberId);
        when(headerProvider.extractAccessToken(bearerToken)).thenReturn(token);
        when(jwtProvider.parsePayload(token, jwtProperties.getAccessSecretKey())).thenReturn(payload);

        for (int i = 0; i < 100; i++) {
            filter.doFilterInternal(request, response, chain);
        }

        // when
        filter.doFilterInternal(request, response, chain);

        // then
        verify(rateLimitExceededHandler).handle(
                eq(request),
                eq(response),
                anyLong()
        );
    }

    @Test
    void 오래된_요청기록을_제거한다() {
        // given
        var memberId = 1L;
        var oldTimestamp = System.nanoTime() - TimeUnit.MINUTES.toNanos(10);

        var deque = new ArrayDeque<Long>();
        deque.add(oldTimestamp);

        filter.getRequestLogs()
                .put(memberId, deque);

        // when
        filter.cleanUpStaleRequestLogsInternal();

        // then
        assertThat(filter.getRequestLogs()).doesNotContainKey(memberId);
    }
}
