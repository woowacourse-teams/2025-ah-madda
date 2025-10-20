package com.ahmadda.infra.notification.mail;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GmailCircuitBreakerRecoverySchedulerTest {

    private GmailCircuitBreakerRecoveryScheduler sut;
    private CircuitBreaker circuitBreaker;
    private GmailHealthChecker gmailHealthChecker;

    @BeforeEach
    void setUp() {
        var registry = CircuitBreakerRegistry.ofDefaults();
        circuitBreaker = registry.circuitBreaker("primaryEmail");
        gmailHealthChecker = mock(GmailHealthChecker.class);
        sut = new GmailCircuitBreakerRecoveryScheduler(registry, gmailHealthChecker);
    }

    @Test
    void CircuitBreaker가_FORCED_OPEN이고_지메일이_정상_상태이면_Closed로_전환된다() {
        // given
        circuitBreaker.transitionToForcedOpenState();
        when(gmailHealthChecker.isAvailable()).thenReturn(true);

        // when
        sut.recoverGmailCircuitBreaker();

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void CircuitBreaker가_FORCED_OPEN이어도_지메일이_비정상이면_상태는_변하지_않는다() {
        // given
        circuitBreaker.transitionToForcedOpenState();
        when(gmailHealthChecker.isAvailable()).thenReturn(false);

        // when
        sut.recoverGmailCircuitBreaker();

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.FORCED_OPEN);
    }

    @Test
    void CircuitBreaker가_FORCED_OPEN이_아니면_체크하지_않는다() {
        // given
        circuitBreaker.transitionToOpenState();
        when(gmailHealthChecker.isAvailable()).thenReturn(true);

        // when
        sut.recoverGmailCircuitBreaker();

        // then
        verifyNoInteractions(gmailHealthChecker);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
