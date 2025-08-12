package com.ahmadda.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ReminderTest {

    @Autowired
    private Reminder sut;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventRepository eventRepository;

    @MockitoBean
    private PushNotifier pushNotifier;

    @MockitoBean
    private EmailNotifier emailNotifier;

    @Test
    void 수신자들에게_이메일과_푸시를_발송한다() {
        // given
        var organization = organizationRepository.save(Organization.create("우테코", "설명", "img.png"));
        var organizerMember = memberRepository.save(Member.create("주최자", "host@example.com", "pic"));
        var organizer =
                organizationMemberRepository.save(OrganizationMember.create("host", organizerMember, organization));

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "타이틀", "내용", "장소", organizer, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(3),
                        now.plusDays(4), now.plusDays(5),
                        now
                ),
                10
        ));

        var m1 = memberRepository.save(Member.create("게스트1", "g1@example.com", "pic"));
        var m2 = memberRepository.save(Member.create("게스트2", "g2@example.com", "pic"));
        var om1 = organizationMemberRepository.save(OrganizationMember.create("g1", m1, organization));
        var om2 = organizationMemberRepository.save(OrganizationMember.create("g2", m2, organization));
        var recipients = List.of(om1, om2);
        var content = "이벤트 알림입니다.";

        // when
        sut.remind(recipients, event, content);

        // then
        verify(emailNotifier).sendEmails(
                eq(recipients),
                argThat(payload -> payload != null && payload.body()
                        .eventId()
                        .equals(event.getId()))
        );
        verify(pushNotifier).sendPushs(
                eq(recipients),
                argThat(payload -> payload != null && payload.eventId()
                        .equals(event.getId()))
        );
    }

    @Test
    void 알람_히스토리가_생성된다() {
        // given
        var organization = organizationRepository.save(Organization.create("우테को", "설명", "img.png"));
        var organizerMember = memberRepository.save(Member.create("주최자", "host@example.com", "pic"));
        var organizer =
                organizationMemberRepository.save(OrganizationMember.create("host", organizerMember, organization));

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "타이틀", "내용", "장소", organizer, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(3),
                        now.plusDays(4), now.plusDays(5),
                        now
                ),
                10
        ));

        var m1 = memberRepository.save(Member.create("게스트1", "g1@example.com", "pic"));
        var m2 = memberRepository.save(Member.create("게스트2", "g2@example.com", "pic"));
        var om1 = organizationMemberRepository.save(OrganizationMember.create("g1", m1, organization));
        var om2 = organizationMemberRepository.save(OrganizationMember.create("g2", m2, organization));
        var recipients = List.of(om1, om2);
        var content = "이벤트 알림입니다.";

        // when
        var history = sut.remind(recipients, event, content);

        // then
        assertSoftly(softly -> {
            softly.assertThat(history.getEvent())
                    .isEqualTo(event);
            softly.assertThat(history.getContent())
                    .isEqualTo(content);
            softly.assertThat(history.getSentAt())
                    .isNotNull();

            softly.assertThat(history.getRecipients())
                    .extracting(ReminderRecipient::getOrganizationMember)
                    .containsExactlyInAnyOrder(om1, om2);
        });
    }
}
