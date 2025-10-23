package com.ahmadda.infra.notification.mail.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GmailCircuitBreakerRecoveryScheduler {

    private final CircuitBreaker circuitBreaker;
    private final GmailHealthChecker gmailHealthChecker;

    public GmailCircuitBreakerRecoveryScheduler(
            final CircuitBreakerRegistry circuitBreakerRegistry,
            final GmailHealthChecker gmailHealthChecker
    ) {
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("primaryEmail");
        this.gmailHealthChecker = gmailHealthChecker;
    }

    @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
    public void recoverGmailCircuitBreaker() {
        if (circuitBreaker.getState() == CircuitBreaker.State.FORCED_OPEN) {
            if (gmailHealthChecker.isAvailable()) {
                circuitBreaker.transitionToClosedState();
            }
        }
    }
}
