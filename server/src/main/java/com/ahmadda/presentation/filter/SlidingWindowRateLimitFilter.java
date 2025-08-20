package com.ahmadda.presentation.filter;

import com.ahmadda.infra.login.jwt.JwtProvider;
import com.ahmadda.presentation.header.HeaderProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@RequiredArgsConstructor
public class SlidingWindowRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 60_000;
    private static final int MAX_REQUESTS = 100;

    private final HeaderProvider headerProvider;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    private final Map<Long, Deque<Long>> requestLogs = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = extractMemberIdSafely(authorizationHeader);

        if (memberId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = Instant.now()
                .toEpochMilli();
        Deque<Long> timestamps = requestLogs.computeIfAbsent(memberId, id -> new ConcurrentLinkedDeque<>());

        if (isRateLimited(timestamps, now)) {
            respondTooManyRequests(request, response, timestamps, now);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(final Deque<Long> timestamps, final long now) {
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < now - WINDOW_MILLIS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                return true;
            }

            timestamps.addLast(now);
            return false;
        }
    }

    private void respondTooManyRequests(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Deque<Long> timestamps,
            final long now
    ) throws IOException {
        long retryAfterSeconds = calculateRetryAfterSeconds(timestamps, now);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setDetail("요청이 너무 많습니다. 약 " + retryAfterSeconds + "초 후 다시 시도해 주세요.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter()
                .write(objectMapper.writeValueAsString(problemDetail));
    }

    private long calculateRetryAfterSeconds(final Deque<Long> timestamps, final long now) {
        synchronized (timestamps) {
            if (timestamps.isEmpty()) {
                return 1;
            }
            long oldest = timestamps.peekFirst();
            long retryMillis = (oldest + WINDOW_MILLIS) - now;
            return Math.max(1, (retryMillis + 999) / 1000);
        }
    }

    private Long extractMemberIdSafely(final String authorizationHeader) {
        try {
            String accessToken = headerProvider.extractAccessToken(authorizationHeader);

            return jwtProvider.parseAccessPayload(accessToken)
                    .getMemberId();
        } catch (Exception e) {
            return null;
        }
    }
}
