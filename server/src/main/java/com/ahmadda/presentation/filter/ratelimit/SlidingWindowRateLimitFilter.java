package com.ahmadda.presentation.filter.ratelimit;

import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import com.ahmadda.presentation.header.HeaderProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
@RequiredArgsConstructor
public class SlidingWindowRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_NANOS = TimeUnit.SECONDS.toNanos(60);
    private static final int MAX_REQUESTS = 100;

    private final HeaderProvider headerProvider;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider;
    private final RateLimitExceededHandler rateLimitExceededHandler;

    private final Map<Long, Deque<Long>> requestLogs = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = extractMemberIdSafely(authorizationHeader);

        if (memberId == null) {
            chain.doFilter(request, response);
            return;
        }

        long now = System.nanoTime();

        RateLimitResult rateLimitResult = (RateLimitResult) requestLogs.compute(
                memberId, (id, timestamps) -> {
                    if (timestamps == null) {
                        timestamps = new ArrayDeque<>();
                    }

                    synchronized (timestamps) {
                        removeExpiredTimestamps(timestamps, now);

                        if (timestamps.size() >= MAX_REQUESTS) {
                            long retryAfterSeconds = calculateRetryAfterSeconds(timestamps, now, id);

                            return new RateLimitResult(timestamps, true, retryAfterSeconds);
                        }

                        timestamps.addLast(now);
                        return new RateLimitResult(timestamps, false, 0);
                    }
                }
        );

        if (rateLimitResult.isRateLimited) {
            rateLimitExceededHandler.handle(request, response, rateLimitResult.retryAfterSeconds);
            return;
        }

        chain.doFilter(request, response);
    }

    public void cleanUpStaleRequestLogsInternal() {
        long now = System.nanoTime();

        requestLogs.entrySet()
                .removeIf(entry -> {
                    Deque<Long> timestamps = entry.getValue();

                    synchronized (timestamps) {
                        removeExpiredTimestamps(timestamps, now);

                        return timestamps.isEmpty();
                    }
                });
    }

    private void removeExpiredTimestamps(final Deque<Long> timestamps, final long now) {
        while (!timestamps.isEmpty() && timestamps.peekFirst() < now - WINDOW_NANOS) {
            timestamps.pollFirst();
        }
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
    private long calculateRetryAfterSeconds(final Deque<Long> timestamps, final long now, final Long memberId) {
        if (timestamps.isEmpty()) {
            log.warn(
                    "SlidingWindowRateLimitFilterError: reRetryAfterSeconds를 계산할 때 timestamps가 비어있습니다. 발생시간={}, memberId={}",
                    now,
                    memberId
            );
            return 1;
        }

        long oldest = timestamps.peekFirst();
        long retryNanos = (oldest + WINDOW_NANOS) - now;

        return Math.max(1, TimeUnit.NANOSECONDS.toSeconds(retryNanos + 999_999_999));
    }

    private Long extractMemberIdSafely(final String authorizationHeader) {
        try {
            String accessToken = headerProvider.extractAccessToken(authorizationHeader);

            return jwtProvider.parsePayload(accessToken, jwtProperties.getAccessSecretKey())
                    .getMemberId();
        } catch (Exception e) {
            return null;
        }
    }

    private static class RateLimitResult extends ArrayDeque<Long> {

        final boolean isRateLimited;
        final long retryAfterSeconds;

        RateLimitResult(final Deque<Long> delegate, final boolean isRateLimited, final long retryAfterSeconds) {
            super(delegate);
            this.isRateLimited = isRateLimited;
            this.retryAfterSeconds = retryAfterSeconds;
        }
    }
}
