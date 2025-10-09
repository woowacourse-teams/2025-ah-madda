package com.ahmadda.infra.notification.mail;

import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;

import java.net.SocketTimeoutException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RetryableEmailSenderTest {

    private RetryableEmailSender sut;
    private EmailSender delegate;
    private List<String> recipients;
    private String subject;
    private String body;

    @BeforeEach
    void setUp() {
        delegate = mock(EmailSender.class);
        var retryRegistry = RetryRegistry.ofDefaults();
        sut = new RetryableEmailSender(delegate, retryRegistry, "testRetry", 3, 10);

        recipients = List.of("user@example.com");
        subject = "테스트 이메일";
        body = "본문 내용입니다.";
    }

    @Test
    void 첫번째_시도에_성공하면_재시도하지_않는다() {
        // when
        sut.sendEmails(recipients, subject, body);

        // then
        verify(delegate, times(1)).sendEmails(recipients, subject, body);
    }

    @Test
    void 첫번째_시도에_실패하더라도_두번째에는_성공한다() {
        // given
        var cause = new SocketTimeoutException("timeout");
        var ex = new MailSendException("mail send failed", cause);

        doThrow(ex)
                .doNothing()
                .when(delegate)
                .sendEmails(recipients, subject, body);

        // when
        sut.sendEmails(recipients, subject, body);

        // then
        verify(delegate, times(2)).sendEmails(recipients, subject, body);
    }

    @Test
    void 최대_시도횟수까지_실패하면_예외가_발생한다() {
        // given
        var cause = new SocketTimeoutException("timeout");
        var ex = new MailSendException("mail send failed", cause);

        doThrow(ex)
                .when(delegate)
                .sendEmails(recipients, subject, body);

        // when & then
        assertThatThrownBy(() -> sut.sendEmails(recipients, subject, body))
                .isInstanceOf(MailSendException.class);

        verify(delegate, times(3)).sendEmails(recipients, subject, body);
    }
}
