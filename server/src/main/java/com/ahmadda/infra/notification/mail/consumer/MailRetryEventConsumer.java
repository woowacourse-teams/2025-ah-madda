package com.ahmadda.infra.notification.mail.consumer;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailRetryEventConsumer implements RegistryEventConsumer<Retry> {

    @Override
    public void onEntryAddedEvent(final EntryAddedEvent<Retry> event) {
        Retry retry = event.getAddedEntry();
        if (isMailRetry(retry)) {
            retry.getEventPublisher()
                    .onRetry(e ->
                            log.warn(
                                    "mailRetry - retryName: {}, attempt: {}, lastThrowable: {}",
                                    retry.getName(),
                                    e.getNumberOfRetryAttempts(),
                                    e.getLastThrowable()
                                            .toString()
                            ))
                    .onSuccess(e ->
                            log.info(
                                    "mailRetrySuccess - retryName: {}, attempts: {}",
                                    retry.getName(),
                                    e.getNumberOfRetryAttempts()
                            ))
                    .onError(e ->
                            log.error(
                                    "mailRetryError - retryName: {}, attempts: {}, lastThrowable: {}",
                                    retry.getName(),
                                    e.getNumberOfRetryAttempts(),
                                    e.getLastThrowable()
                                            .toString(),
                                    e.getLastThrowable()
                            ));
        }
    }

    @Override
    public void onEntryRemovedEvent(final EntryRemovedEvent<Retry> event) {
    }

    @Override
    public void onEntryReplacedEvent(final EntryReplacedEvent<Retry> event) {
    }

    private boolean isMailRetry(final Retry retry) {
        return "smtpEmail".equals(retry.getName());
    }
}
