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
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@RequiredArgsConstructor
public class SlidingWindowRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_NANOS = TimeUnit.MILLISECONDS.toNanos(60_000);
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

        long now = System.nanoTime();
        Deque<Long> timestamps = requestLogs.computeIfAbsent(memberId, id -> new ConcurrentLinkedDeque<>());

        if (isRateLimited(timestamps, now)) {
            respondTooManyRequests(request, response, timestamps, now);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(final Deque<Long> timestamps, final long now) {
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < now - WINDOW_NANOS) {
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
        problemDetail.setDetail("요청이 너무 많습니다." + retryAfterSeconds + "초 후 다시 시도해 주세요.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter()
                .write(objectMapper.writeValueAsString(problemDetail));
    }

    /**
     * 현재 요청이 거부된 경우, 클라이언트가 다음 요청을 시도할 수 있는 시점까지의 대기 시간을 계산한다.
     * <p>
     * 요청 시각(now)과 가장 오래된 요청 시각(oldest) 간의 차이를 기반으로,
     * 슬라이딩 윈도우 범위(WINDOW_NANOS)가 지난 시점을 계산하고,
     * 그 시간까지 남은 대기 시간을 초 단위로 반환한다.
     *
     * @param timestamps 해당 사용자의 요청 타임스탬프 목록 (nanoTime 기준)
     * @param now        현재 시각 (System.nanoTime() 기준)
     * @return 다음 요청이 허용되기까지 남은 시간 (초). 최소 1초 이상으로 보정됨.
     */
    private long calculateRetryAfterSeconds(final Deque<Long> timestamps, final long now) {
        synchronized (timestamps) {
            if (timestamps.isEmpty()) {
                return 1;
            }

            long oldest = timestamps.peekFirst();
            long retryNanos = (oldest + WINDOW_NANOS) - now;

            return Math.max(1, TimeUnit.NANOSECONDS.toSeconds(retryNanos + 999_999_999));
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
