package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.NonGuestsNotificationRequest;
import com.ahmadda.application.dto.SelectedOrganizationMembersNotificationRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.EmailNotifier;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventEmailPayload;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.PushNotificationPayload;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.infra.notification.push.FcmPushToken;
import com.ahmadda.infra.notification.push.FcmPushTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
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

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private FcmPushTokenRepository fcmPushTokenRepository;

    @MockitoBean
    private EmailNotifier emailNotifier;

    @MockitoBean
    private PushNotifier pushNotifier;

    @Test
    void 비게스트_조직원에게_알람을_전송한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
        var now = LocalDateTime.now();

        var event = eventRepository.save(Event.create(
                "이벤트제목", "설명", "장소",
                organizer, organization,
                EventOperationPeriod.create(
                        now.minusDays(2), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(3)
                ),
                organizer.getNickname(), 100
        ));

        var request = createNotificationRequest();

        var guest = createAndSaveOrganizationMember("게스트", "guest@email.com", organization);
        guestRepository.save(Guest.create(event, guest, event.getRegistrationStart()));

        var ng1 = createAndSaveOrganizationMember("비게스트1", "ng1@email.com", organization);
        var ng2 = createAndSaveOrganizationMember("비게스트2", "ng2@email.com", organization);

        savePushToken(ng1, "token-ng1");
        savePushToken(ng2, "token-ng2");

        // when
        sut.notifyNonGuestOrganizationMembers(event.getId(), request, createLoginMember(organizer));

        // then
        var email = EventEmailPayload.of(event, request.content());
        var pushPayload = PushNotificationPayload.of(event, request.content());

        verify(emailNotifier).sendEmails(List.of("ng1@email.com", "ng2@email.com"), email);
        verify(pushNotifier).sendPushs(List.of("token-ng1", "token-ng2"), pushPayload);
    }

    @Test
    void 존재하지_않는_이벤트로_메일_전송시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
        var loginMember = createLoginMember(organizer);

        // when // then
        assertThatThrownBy(() -> sut.notifyNonGuestOrganizationMembers(
                999L,
                createNotificationRequest(),
                loginMember
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 주최자가_아닌_회원이_메일을_전송하면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
        var other = createAndSaveOrganizationMember("다른사람", "other@email.com", organization);
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
                organizer.getNickname(),
                100
        ));

        // when // then
        assertThatThrownBy(() -> sut.notifyNonGuestOrganizationMembers(
                event.getId(),
                createNotificationRequest(),
                createLoginMember(other)
        ))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("이벤트 주최자가 아닙니다.");
    }


    @Test
    void 선택된_조직원에게_알람을_전송한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
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
                organizer.getNickname(),
                100
        ));

        var om1 = createAndSaveOrganizationMember("선택1", "sel1@email.com", organization);
        var om2 = createAndSaveOrganizationMember("선택2", "sel2@email.com", organization);
        var om3 = createAndSaveOrganizationMember("비선택", "nsel@email.com", organization);

        savePushToken(om1, "token-ng1");
        savePushToken(om2, "token-ng2");

        var request = createSelectedMembersRequest(List.of(om1.getId(), om2.getId()));
        var email = EventEmailPayload.of(event, request.content());
        var pushPayload = PushNotificationPayload.of(event, request.content());

        // when
        sut.notifySelectedOrganizationMembers(event.getId(), request, createLoginMember(organizer));

        // then
        verify(emailNotifier).sendEmails(List.of("sel1@email.com", "sel2@email.com"), email);
        verify(pushNotifier).sendPushs(List.of("token-ng1", "token-ng2"), pushPayload);
    }

    @Test
    void 존재하지_않는_이벤트로_알람_전송시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
        var om = createAndSaveOrganizationMember("대상자", "target@email.com", organization);
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
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);
        var other = createAndSaveOrganizationMember("다른사람", "other@email.com", organization);

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
                organizer.getNickname(),
                100
        ));

        var om = createAndSaveOrganizationMember("대상자", "target@email.com", organization);
        var request = createSelectedMembersRequest(java.util.List.of(om.getId()));

        // when // then
        assertThatThrownBy(() ->
                sut.notifySelectedOrganizationMembers(event.getId(), request, createLoginMember(other))
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("이벤트 주최자가 아닙니다.");
    }

    @Test
    void 요청에_존재하지_않는_조직원_ID가_포함되면_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer = createAndSaveOrganizationMember("주최자", "host@email.com", organization);

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
                organizer.getNickname(),
                100
        ));

        var validOm = createAndSaveOrganizationMember("유효", "valid@email.com", organization);

        var otherOrg = organizationRepository.save(Organization.create("다른조직", "설명", "img2.png"));
        var otherOm = createAndSaveOrganizationMember("다른조직원", "otherorg@email.com", otherOrg);

        var request = createSelectedMembersRequest(java.util.List.of(validOm.getId(), otherOm.getId()));

        // when // then
        assertThatThrownBy(() ->
                sut.notifySelectedOrganizationMembers(event.getId(), request, createLoginMember(organizer))
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    private NonGuestsNotificationRequest createNotificationRequest() {
        return new NonGuestsNotificationRequest("이메일 내용입니다.");
    }

    private SelectedOrganizationMembersNotificationRequest createSelectedMembersRequest(List<Long> organizationMemberIds) {
        return new SelectedOrganizationMembersNotificationRequest(organizationMemberIds, "이메일 내용입니다.");
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            String email,
            Organization organization
    ) {
        var member = memberRepository.save(Member.create(nickname, email));

        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, organization));
    }

    private LoginMember createLoginMember(OrganizationMember organizationMember) {
        var member = organizationMember.getMember();

        return new LoginMember(member.getId());
    }

    private void savePushToken(OrganizationMember organizationMember, String token) {
        var fcmPushToken = FcmPushToken.create(
                organizationMember.getMember()
                        .getId(), token, LocalDateTime.now()
        );

        fcmPushTokenRepository.save(fcmPushToken);
    }
}
