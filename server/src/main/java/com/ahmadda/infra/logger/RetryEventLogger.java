package com.ahmadda.infra.logger;

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
                                "retry - name: {}, attempt: {}, lastThrowable: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts(),
                                e.getLastThrowable()
                                        .toString()
                        ))
                .onSuccess(e ->
                        log.info(
                                "retrySuccess - name: {}, attempts: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts()
                        ))
                .onError(e ->
                        log.error(
                                "retryError - name: {}, attempts: {}, lastThrowable: {}",
                                retry.getName(),
                                e.getNumberOfRetryAttempts(),
                                e.getLastThrowable()
                                        .toString(),
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
