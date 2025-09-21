package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.OrganizationMember;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.List;

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
    public void sendEmails(final List<OrganizationMember> recipients, final EventEmailPayload payload) {
        Runnable runnable = Retry.decorateRunnable(
                retry,
                () -> delegate.sendEmails(recipients, payload)
        );

        try {
            runnable.run();
        } catch (Exception ex) {
            log.error(
                    "mailRetryError - name: {}, recipients: {}, subject: {}, cause: {}",
                    retry.getName(),
                    recipients.size(),
                    payload.subject(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        }
    }

    private boolean isRetryable(final Throwable ex) {
        return unwrap(ex, SocketTimeoutException.class) != null
                || unwrap(ex, MailException.class) != null;
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
