package com.ahmadda.presentation.filter.ratelimit;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlidingWindowRequestLogCleaner {

    private final SlidingWindowRateLimitFilter rateLimitFilter;

    @Scheduled(cron = "0 */10 * * * *")
    @SchedulerLock(
            name = "cleanUpStaleRequestLogs",
            lockAtMostFor = "6m",
            lockAtLeastFor = "3m"
    )
    public void cleanUpStaleRequestLogs() {
        rateLimitFilter.cleanUpStaleRequestLogsInternal();
    }
}
