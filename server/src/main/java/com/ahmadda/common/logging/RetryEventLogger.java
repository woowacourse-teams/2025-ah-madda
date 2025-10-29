package com.ahmadda.common.logging;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryEventLogger implements RegistryEventConsumer<Retry> {

    @Override
    public void onEntryAddedEvent(final EntryAddedEvent<Retry> event) {
        Retry retry = event.getAddedEntry();
        retry.getEventPublisher()
                .onRetry(e ->
                        log.warn(
                                "retryAttempt - name: {}, attempt: {}, cause: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts(),
                                e.getLastThrowable()
                                        .getMessage(),
                                e.getLastThrowable()
                        ))
                .onSuccess(e ->
                        log.info(
                                "retrySuccess - name: {}, attempts: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts()
                        ))
                .onError(e ->
                        log.error(
                                "retryError - name: {}, attempts: {}, cause: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts(),
                                e.getLastThrowable()
                                        .getMessage(),
                                e.getLastThrowable()
                        ));
    }

    @Override
    public void onEntryRemovedEvent(final EntryRemovedEvent<Retry> event) {
    }

    @Override
    public void onEntryReplacedEvent(final EntryReplacedEvent<Retry> event) {
    }
}
