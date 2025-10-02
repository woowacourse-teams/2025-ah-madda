package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.ReminderEmail;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketTimeoutException;
import java.time.Duration;

@Slf4j
public class RetryableEmailNotifier implements EmailNotifier {

    private final EmailNotifier delegate;
    private final Retry retry;

    public RetryableEmailNotifier(
            final RetryRegistry retryRegistry,
            final String retryName,
            final EmailNotifier delegate,
            final int maxAttempts,
            final long waitMillis
    ) {
        this.delegate = delegate;
        this.retry = retryRegistry.retry(
                retryName,
                RetryConfig.custom()
                        .maxAttempts(maxAttempts)
                        .waitDuration(Duration.ofMillis(waitMillis))
                        .retryOnException(this::isRetryable)
                        .failAfterMaxAttempts(true)
                        .build()
        );
    }

    @Override
    public void sendEmail(final ReminderEmail reminderEmail) {
        Runnable runnable = Retry.decorateRunnable(
                retry,
                () -> delegate.sendEmail(reminderEmail)
        );

        try {
            runnable.run();
        } catch (Exception ex) {
            log.error(
                    "mailRetryError - name: {}, reminderEmail: {}, cause: {}",
                    retry.getName(),
                    reminderEmail,
                    ex.getMessage(),
                    ex
            );
            throw ex;
        }
    }

    private boolean isRetryable(final Throwable ex) {
        return unwrap(ex, SocketTimeoutException.class) != null;
    }

    private <T extends Throwable> T unwrap(final Throwable ex, final Class<T> target) {
        Throwable current = ex;
        while (current != null) {
            if (target.isInstance(current)) {
                return target.cast(current);
            }
            current = current.getCause();
        }

        return null;
    }
}
