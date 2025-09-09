package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.notification.EventNotificationOptOut;
import com.ahmadda.domain.notification.EventNotificationOptOutRepository;
import com.ahmadda.domain.notification.Reminder;
import com.ahmadda.domain.notification.ReminderHistory;
import com.ahmadda.domain.notification.ReminderHistoryRepository;
import com.ahmadda.domain.notification.ReminderRecipient;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.verify;

@IntegrationTest
class EventNotificationServiceTest {

    @Autowired
    private EventNotificationService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @MockitoSpyBean
    private Reminder reminder;

    @Autowired
    private ReminderHistoryRepository reminderHistoryRepository;

    @Autowired
    private EventNotificationOptOutRepository eventNotificationOptOutRepository;

    @MockitoBean
    JwtAccessTokenProperties accessTokenProperties;

    @MockitoBean
    JwtRefreshTokenProperties refreshTokenProperties;

    @Test
    void 선택된_구성원에게_알람을_전송한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var om1 = saveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = saveOrganizationMember("선택2", "sel2@email.com", organization);
        saveOrganizationMember("비선택", "nsel@email.com", organization);

        var request = createSelectedMembersRequest(List.of(om1.getId(), om2.getId()));

        // when
        sut.notifySelectedOrganizationMembers(event.getId(), request, createLoginMember(organizer));

        // then
        verify(reminder).remind(List.of(om1, om2), event, request.content());
    }

    @Test
    void 선택된_구성원에게_알람_전송_후_히스토리가_저장된다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목", "설명", "장소",
                organizer, organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var om1 = saveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = saveOrganizationMember("선택2", "sel2@email.com", organization);
        var notSelected = saveOrganizationMember("비선택", "nsel@email.com", organization);

        var request = createSelectedMembersRequest(List.of(om1.getId(), om2.getId()));
        var content = request.content();

        // when
        sut.notifySelectedOrganizationMembers(event.getId(), request, createLoginMember(organizer));

        // then
        var saved = reminderHistoryRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(saved)
                    .hasSize(1);

            var history = saved.get(0);
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

    @Test
    void 존재하지_않는_이벤트로_알람_전송시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var om = saveOrganizationMember("대상자", "target@email.com", organization);
        var request = createSelectedMembersRequest(java.util.List.of(om.getId()));

        // when // then
        assertThatThrownBy(() ->
                                   sut.notifySelectedOrganizationMembers(999L, request, createLoginMember(organizer))
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 주최자가_아닌_회원이_전송하면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var other = saveOrganizationMember("다른사람", "other@email.com", organization);

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(1), now.plusDays(1),
                        now.plusDays(2), now.plusDays(3),
                        now.minusDays(2)
                ),
                100
        ));

        var om = saveOrganizationMember("대상자", "target@email.com", organization);
        var request = createSelectedMembersRequest(java.util.List.of(om.getId()));

        // when // then
        assertThatThrownBy(() ->
                                   sut.notifySelectedOrganizationMembers(
                                           event.getId(),
                                           request,
                                           createLoginMember(other)
                                   )
        )
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 주최자가 아닙니다.");
    }

    @Test
    void 알림_내용이_20자를_초과하면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var om1 = saveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = saveOrganizationMember("선택2", "sel2@email.com", organization);

        var overLengthContent = "이 메시지는 20자를 초과합니다. 예외를 발생시켜야 합니다.";
        var request = new SelectedOrganizationMembersNotificationRequest(
                List.of(om1.getId(), om2.getId()),
                overLengthContent
        );

        // when // then
        assertThatThrownBy(() ->
                                   sut.notifySelectedOrganizationMembers(
                                           event.getId(),
                                           request,
                                           createLoginMember(organizer)
                                   )
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("알림 메시지는 20자 이하여야 합니다.");
    }


    @Test
    void 요청에_존재하지_않는_구성원_ID가_포함되면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);

        var now = LocalDateTime.now();
        var event = eventRepository.save(Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(1), now.plusDays(1),
                        now.plusDays(2), now.plusDays(3),
                        now.minusDays(2)
                ),
                100
        ));

        var validOm = saveOrganizationMember("유효", "valid@email.com", organization);

        var otherOrg = organizationRepository.save(Organization.create("다른 이벤트 스페이스", "설명", "img2.png"));
        var otherOm = saveOrganizationMember("다른구성원", "otherorg@email.com", otherOrg);

        var request = createSelectedMembersRequest(java.util.List.of(validOm.getId(), otherOm.getId()));

        // when // then
        assertThatThrownBy(() ->
                                   sut.notifySelectedOrganizationMembers(
                                           event.getId(),
                                           request,
                                           createLoginMember(organizer)
                                   )
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 리마인더를_30분_내에_10번_보냈다면_11번째_요청은_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var om1 = saveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = saveOrganizationMember("선택2", "sel2@email.com", organization);
        var request = createSelectedMembersRequest(List.of(om1.getId(), om2.getId()));

        for (int i = 0; i < 10; i++) {
            reminderHistoryRepository.save(
                    ReminderHistory.createNow(event, request.content(), List.of(om1, om2))
            );
        }

        // when // then
        assertThatThrownBy(() ->
                                   sut.notifySelectedOrganizationMembers(
                                           event.getId(),
                                           request,
                                           createLoginMember(organizer)
                                   )
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageStartingWith("리마인더는 30분 내 최대 10회까지만 발송할 수 있습니다.");
    }

    @Test
    void 선택된_구성원중_수신_거부한_구성원이_있으면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var organizer = saveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목", "설명", "장소",
                organizer, organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                100
        ));

        var om1 = saveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = saveOrganizationMember("선택2", "sel2@email.com", organization);

        eventNotificationOptOutRepository.save(EventNotificationOptOut.create(om2, event));

        var request = createSelectedMembersRequest(List.of(om1.getId(), om2.getId()));

        // when // then
        assertThatThrownBy(() -> sut.notifySelectedOrganizationMembers(
                                   event.getId(),
                                   request,
                                   createLoginMember(organizer)
                           )
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("선택된 구성원 중 알림 수신 거부자가 존재합니다.");
    }

    private SelectedOrganizationMembersNotificationRequest createSelectedMembersRequest(List<Long> organizationMemberIds) {
        return new SelectedOrganizationMembersNotificationRequest(organizationMemberIds, "이메일 내용입니다.");
    }

    private OrganizationMember saveOrganizationMember(
            String nickname,
            String email,
            Organization organization
    ) {
        var member = memberRepository.save(Member.create(nickname, email, "testPicture"));

        return organizationMemberRepository.save(OrganizationMember.create(
                nickname,
                member,
                organization,
                OrganizationMemberRole.USER
        ));
    }

    private LoginMember createLoginMember(OrganizationMember organizationMember) {
        var member = organizationMember.getMember();

        return new LoginMember(member.getId());
    }
}
