package com.ahmadda.infra.notification.mail.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class GmailQuotaCircuitBreakerHandlerTest {

    private GmailQuotaCircuitBreakerHandler sut;
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        var registry = CircuitBreakerRegistry.ofDefaults();
        sut = new GmailQuotaCircuitBreakerHandler(registry);
        sut.registerQuotaExceededHandler();
        circuitBreaker = registry.circuitBreaker("primaryEmail");
    }

    @Test
    void 지메일_일일한도초과_에러가_발생하면_CircuitBreaker는_FORCED_OPEN_상태가_된다() {
        // given
        var quotaExceeded = new RuntimeException("SMTP 550-5.4.5 Daily limit exceeded");

        // when
        circuitBreaker.onError(0, TimeUnit.MILLISECONDS, quotaExceeded);

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.FORCED_OPEN);
    }

    @Test
    void 지메일_일일한도초과가_아닌_에러는_CircuitBreaker를_FORCED_OPEN으로_전환하지_않는다() {
        // given
        var otherError = new RuntimeException("452-4.5.3 Too many recipients");

        // when
        circuitBreaker.onError(0, TimeUnit.MILLISECONDS, otherError);

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }
}
