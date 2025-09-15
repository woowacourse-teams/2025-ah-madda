package com.ahmadda.infra.notification.mail.consumer;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailCircuitBreakerEventConsumer implements RegistryEventConsumer<CircuitBreaker> {

    @Override
    public void onEntryAddedEvent(final EntryAddedEvent<CircuitBreaker> event) {
        CircuitBreaker cb = event.getAddedEntry();
        if (isMailBreaker(cb)) {
            cb.getEventPublisher()
                    .onStateTransition(e ->
                            log.warn(
                                    "mailCircuitBreakerStateTransition - breaker: {}, from: {}, to: {}",
                                    cb.getName(),
                                    e.getStateTransition()
                                            .getFromState(),
                                    e.getStateTransition()
                                            .getToState()
                            ))
                    .onError(e ->
                            log.error(
                                    "mailCircuitBreakerError - breaker: {}, message: {}",
                                    cb.getName(),
                                    e.getThrowable()
                                            .getMessage(),
                                    e.getThrowable()
                            ))
                    .onCallNotPermitted(e ->
                            log.warn("mailCircuitBreakerCallNotPermitted - breaker: {}", cb.getName()));
        }
    }

    @Override
    public void onEntryRemovedEvent(final EntryRemovedEvent<CircuitBreaker> event) {
    }

    @Override
    public void onEntryReplacedEvent(final EntryReplacedEvent<CircuitBreaker> event) {
    }

    private boolean isMailBreaker(final CircuitBreaker cb) {
        return "primaryEmail".equals(cb.getName());
    }
}
