package com.ahmadda.infra.notification.mail;

import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RetryableEmailNotifierTest {

    private RetryableEmailNotifier sut;

    private EmailNotifier delegate;
    private EventEmailPayload payload;
    private List<OrganizationMember> recipients;

    @BeforeEach
    void setUp() {
        delegate = mock(EmailNotifier.class);
        var retryRegistry = RetryRegistry.ofDefaults();
        sut = new RetryableEmailNotifier(
                retryRegistry,
                "testRetry",
                delegate,
                3,
                10
        );

        payload = new EventEmailPayload(
                new EventEmailPayload.Subject("이벤트 스페이스", "이벤트"),
                new EventEmailPayload.Body(
                        "본문",
                        "이벤트 스페이스",
                        "이벤트",
                        "주최자",
                        "루터회관",
                        LocalDateTime.now()
                                .plusDays(1),
                        LocalDateTime.now()
                                .plusDays(2),
                        LocalDateTime.now()
                                .plusDays(3),
                        LocalDateTime.now()
                                .plusDays(4),
                        1L,
                        1L
                )
        );

        var org = Organization.create("이벤트 스페이스", "설명", "logo.png");
        var member = Member.create("닉네임", "user@example.com", "pic.png");
        recipients = List.of(OrganizationMember.create(
                "닉네임",
                member,
                org,
                OrganizationMemberRole.USER,
                OrganizationGroup.create("그룹")
        ));
    }

    @Test
    void 첫번째_시도에_성공하면_재시도하지_않는다() {
        // when
        sut.sendEmails(recipients, payload);

        // then
        verify(delegate, times(1)).sendEmails(recipients, payload);
    }

    @Test
    void 첫번째_시도에_실패하더라도_두번째에는_성공한다() {
        // given
        var cause = new SocketTimeoutException("timeout");
        var ex = new MailSendException("mail send failed", cause);

        doThrow(ex)
                .doNothing()
                .when(delegate)
                .sendEmails(recipients, payload);

        // when
        sut.sendEmails(recipients, payload);

        // then
        verify(delegate, times(2)).sendEmails(recipients, payload);
    }

    @Test
    void 최대_시도횟수까지_실패하면_예외가_발생한다() {
        // given
        var cause = new SocketTimeoutException("timeout");
        var ex = new MailSendException("mail send failed", cause);

        doThrow(ex)
                .when(delegate)
                .sendEmails(recipients, payload);

        // when & then
        assertThatThrownBy(() -> sut.sendEmails(recipients, payload))
                .isInstanceOf(MailSendException.class);

        verify(delegate, times(3)).sendEmails(recipients, payload);
    }
}
