package com.ahmadda.infra.logger;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CircuitBreakerEventLogger implements RegistryEventConsumer<CircuitBreaker> {

    @Override
    public void onEntryAddedEvent(final EntryAddedEvent<CircuitBreaker> event) {
        CircuitBreaker cb = event.getAddedEntry();
        cb.getEventPublisher()
                .onStateTransition(e ->
                        log.warn(
                                "circuitBreakerStateTransition - name: {}, from: {}, to: {}",
                                cb.getName(),
                                e.getStateTransition()
                                        .getFromState(),
                                e.getStateTransition()
                                        .getToState()
                        ))
                .onError(e ->
                        log.error(
                                "circuitBreakerError - name: {}, cause: {}",
                                cb.getName(),
                                e.getThrowable()
                                        .getMessage(),
                                e.getThrowable()
                        ))
                .onSlowCallRateExceeded(e ->
                        log.warn(
                                "circuitBreakerSlowCallRateExceeded - name: {}",
                                cb.getName()
                        ))
                .onCallNotPermitted(e ->
                        log.warn(
                                "circuitBreakerCallNotPermitted - name: {}",
                                cb.getName()
                        ));
    }

    @Override
    public void onEntryRemovedEvent(final EntryRemovedEvent<CircuitBreaker> event) {
    }

    @Override
    public void onEntryReplacedEvent(final EntryReplacedEvent<CircuitBreaker> event) {
    }
}
