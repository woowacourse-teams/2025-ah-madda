package com.ahmadda.presentation.filter.ratelimit;

import io.github.bucket4j.mysql.MySQLSelectForUpdateBasedProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BucketExpirationCleaner {

    private static final int MAX_TO_REMOVE_IN_ONE_TRANSACTION = 1000;
    private static final int THRESHOLD_TO_CONTINUE_REMOVING = 50;

    private final MySQLSelectForUpdateBasedProxyManager<Long> bucketProxyManager;

    // TODO. 추후 짧은 주기로 실행해야 할 경우 직접 락 제어 고려
    @Scheduled(cron = "0 30 4 * * *")
    @SchedulerLock(
            name = "cleanExpiredBuckets",
            lockAtLeastFor = "5m",
            lockAtMostFor = "15m"
    )
    public void cleanExpiredBuckets() {
        int removed;
        do {
            removed = bucketProxyManager.removeExpired(MAX_TO_REMOVE_IN_ONE_TRANSACTION);
        } while (removed > THRESHOLD_TO_CONTINUE_REMOVING);
    }
}
