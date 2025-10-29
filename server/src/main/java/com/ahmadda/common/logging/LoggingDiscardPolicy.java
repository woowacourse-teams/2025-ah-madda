package com.ahmadda.common.logging;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class LoggingDiscardPolicy implements RejectedExecutionHandler {

    private final String poolName;

    public LoggingDiscardPolicy(final String poolName) {
        this.poolName = poolName;
    }

    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
        log.warn(
                "threadPoolRejected - poolName: {}, activeCount: {}, queueSize: {}, task: {}",
                poolName,
                executor.getActiveCount(),
                executor.getQueue()
                        .size(),
                r.getClass()
                        .getSimpleName()
        );
    }
}
