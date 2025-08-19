package com.ahmadda.infra.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScheduledTraceLoggingAspect {

    private static final String TRACE_ID = "trace_id";

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object setTraceIdForScheduled(final ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put(TRACE_ID, generateTraceId());
        try {
            return joinPoint.proceed();
        } finally {
            MDC.clear();
        }
    }

    private String generateTraceId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}
