package com.ahmadda.learning.infra.notification;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.EmailNotifier;
import com.ahmadda.domain.notification.EventEmailPayload;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.notification.mail.SmtpEmailNotifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled
@IntegrationTest
@TestPropertySource(properties = "mail.mock=false")
class FailoverEmailNotifierTest {

    @Autowired
    private EmailNotifier sut;

    @MockitoSpyBean(name = "gmailSmtpEmailNotifier")
    private SmtpEmailNotifier gmailNotifier;

    @MockitoSpyBean(name = "awsSmtpEmailNotifier")
    private SmtpEmailNotifier awsNotifier;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 실제_Failover_구조에서_메일을_발송한다() {
        // given
        var organizationMember = createOrganizationMember(
                "테스트 이벤트 스페이스",
                "주최자",
                "test@example.com",
                "주최자닉네임"
        );

        var payload = createPayload("테스트 이벤트 스페이스", "테스트 이벤트", "주최자닉네임");

        // when // then
        sut.sendEmails(List.of(organizationMember), payload);
    }

    @Test
    void PrimarySMTP_실패시_SecondarySMTP로_Failover된다() {
        // given
        var organizationMember = createOrganizationMember("테스트 이벤트 스페이스", "주최자", "test@example.com", "닉네임");
        var payload = createPayload("테스트 이벤트 스페이스", "이벤트", "닉네임");

        doThrow(new MailSendException("gmail 실패"))
                .when(gmailNotifier)
                .sendEmails(any(), any());

        // when
        sut.sendEmails(List.of(organizationMember), payload);

        // then
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(
                        gmailNotifier,
                        times(3)
                ).sendEmails(any(), any()));
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(
                        awsNotifier,
                        times(1)
                ).sendEmails(any(), any()));
    }

    @Test
    void PrimarySMTP가_여러번_실패하면_CircuitBreaker가_OPEN되어_바로_Fallback된다() {
        // given
        var organizationMember = createOrganizationMember("테스트 이벤트 스페이스", "주최자", "test@example.com", "닉네임");
        var payload = createPayload("테스트 이벤트 스페이스", "이벤트", "닉네임");

        doThrow(new MailSendException("gmail 실패"))
                .when(gmailNotifier)
                .sendEmails(any(), any());

        // 3번 실패 → CircuitBreaker OPEN
        for (int i = 0; i < 3; i++) {
            try {
                sut.sendEmails(List.of(organizationMember), payload);
                Thread.sleep(7000);
            } catch (Exception ignored) {
            }
        }

        // when - 다시 호출 → retry 없이 바로 fallback
        sut.sendEmails(List.of(organizationMember), payload);

        // then
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(
                        gmailNotifier,
                        times(9)
                ).sendEmails(any(), any()));
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(
                        awsNotifier,
                        times(4)
                ).sendEmails(any(), any()));
    }

    @Test
    void Primary와_SecondarySMTP모두_실패시_예외발생() {
        // given
        var organizationMember = createOrganizationMember("테스트 이벤트 스페이스", "주최자", "test@example.com", "닉네임");
        var payload = createPayload("테스트 이벤트 스페이스", "이벤트", "닉네임");

        doThrow(new MailSendException("gmail 실패"))
                .when(gmailNotifier)
                .sendEmails(any(), any());
        doThrow(new MailSendException("ses 실패"))
                .when(awsNotifier)
                .sendEmails(any(), any());

        // when // then
        assertThatThrownBy(() -> sut.sendEmails(List.of(organizationMember), payload))
                .isInstanceOf(Exception.class);

        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(gmailNotifier, times(3)).sendEmails(any(), any()));
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(awsNotifier, times(3)).sendEmails(any(), any()));
    }

    private OrganizationMember createOrganizationMember(
            final String organizationName,
            final String memberName,
            final String memberEmail,
            final String nickname
    ) {
        var organization = organizationRepository.save(
                Organization.create(organizationName, "설명", "logo.png")
        );
        var member = memberRepository.save(
                Member.create(memberName, memberEmail, "testPicture")
        );
        return organizationMemberRepository.save(
                OrganizationMember.create(nickname, member, organization, OrganizationMemberRole.USER)
        );
    }

    private EventEmailPayload createPayload(
            final String organizationName,
            final String eventTitle,
            final String organizerNickname
    ) {
        return new EventEmailPayload(
                new EventEmailPayload.Subject(organizationName, eventTitle),
                new EventEmailPayload.Body(
                        "테스트 메일 본문입니다.",
                        organizationName,
                        eventTitle,
                        organizerNickname,
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
    }
}
