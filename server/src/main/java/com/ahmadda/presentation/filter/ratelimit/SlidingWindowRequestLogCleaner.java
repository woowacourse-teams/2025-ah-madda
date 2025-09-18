package com.ahmadda.presentation.filter.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlidingWindowRequestLogCleaner {

    private final SlidingWindowRateLimitFilter rateLimitFilter;

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void cleanUpStaleRequestLogs() {
        rateLimitFilter.cleanUpStaleRequestLogsInternal();
    }
}
