package com.ahmadda.presentation.filter.ratelimit;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlidingWindowRequestLogCleaner {

    private final SlidingWindowRateLimitFilter rateLimitFilter;

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    @SchedulerLock(
            name = "cleanUpStaleRequestLogs",
            lockAtMostFor = "2m",
            lockAtLeastFor = "10s"
    )
    public void cleanUpStaleRequestLogs() {
        rateLimitFilter.cleanUpStaleRequestLogsInternal();
    }
}
