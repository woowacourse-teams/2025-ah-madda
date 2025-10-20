package com.ahmadda.infra.notification.mail;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class GmailQuotaCircuitBreakerHandler {

    private static final String DAILY_LIMIT_EXCEEDED_CODE = "550-5.4.5";

    private final CircuitBreaker circuitBreaker;

    public GmailQuotaCircuitBreakerHandler(final CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("primaryEmail");
    }

    @PostConstruct
    public void registerQuotaExceededHandler() {
        circuitBreaker.getEventPublisher()
                .onError(event -> {
                    Throwable cause = event.getThrowable();
                    if (cause.getMessage() != null && cause.getMessage()
                            .contains(DAILY_LIMIT_EXCEEDED_CODE)) {
                        circuitBreaker.transitionToForcedOpenState();
                    }
                });
    }
}
