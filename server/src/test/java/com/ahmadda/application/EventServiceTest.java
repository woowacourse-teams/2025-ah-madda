package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.EventUpdateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.QuestionCreateRequest;
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
import com.ahmadda.domain.PushNotificationRecipient;
import com.ahmadda.domain.PushNotificationRecipientRepository;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.domain.Question;
import com.ahmadda.domain.exception.UnauthorizedOperationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventService sut;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private PushNotificationRecipientRepository pushNotificationRecipientRepository;

    @MockitoBean
    private EmailNotifier emailNotifier;

    @MockitoBean
    private PushNotifier pushNotifier;

    @Test
    void 이벤트를_생성할_수_있다() {
        //given
        var member = createMember();
        var organization = createOrganization();
        var organizationMember = createOrganizationMember(organization, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        var loginMember = new LoginMember(member.getId());

        //when
        var event = sut.createEvent(organization.getId(), loginMember, eventCreateRequest, now);

        //then
        assertThat(eventRepository.findById(event.getId()))
                .isPresent()
                .hasValueSatisfying(savedEvent -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedEvent.getTitle())
                                .isEqualTo("UI/UX 이벤트");
                        softly.assertThat(savedEvent.getDescription())
                                .isEqualTo("UI/UX 이벤트 입니다");
                        softly.assertThat(savedEvent.getPlace())
                                .isEqualTo("선릉");
                        softly.assertThat(savedEvent.getOrganization())
                                .isEqualTo(organization);
                        softly.assertThat(savedEvent.getOrganizer())
                                .isEqualTo(organizationMember);
                        softly.assertThat(savedEvent.getEventOperationPeriod())
                                .isEqualTo(EventOperationPeriod.create(
                                        now, now.plusDays(4),
                                        now.plusDays(5), now.plusDays(6),
                                        now
                                ));
                        List<Question> questions = savedEvent.getQuestions();
                        softly.assertThat(questions)
                                .hasSize(2)
                                .extracting("questionText", "isRequired", "orderIndex")
                                .containsExactly(Tuple.tuple("1번 질문", true, 0), Tuple.tuple("2번 질문", false, 1));
                    });
                });
    }

    @Test
    void 이벤트_생성시_조직_id에_해당하는_조직이_없다면_예외가_발생한다() {
        //given
        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        var loginMember = new LoginMember(1L);

        //when //then
        assertThatThrownBy(() -> sut.createEvent(999L, loginMember, eventCreateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 조직 정보입니다.");
    }

    @Test
    void 이벤트_생성시_조직원_id에_해당하는_조직원이_없다면_예외가_발생한다() {
        //given
        var organization = createOrganization();

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        var loginMember = new LoginMember(999L);

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization.getId(), loginMember, eventCreateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_생성시_요청한_조직에_소속되지_않았다면_예외가_발생한다() {
        //given
        var organization1 = createOrganization();
        var organization2 = createOrganization();
        var member = createMember();
        createOrganizationMember(organization2, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        var loginMember = new LoginMember(member.getId());

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization1.getId(), loginMember, eventCreateRequest, now))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("조직에 소속되지 않은 회원입니다.");
    }

    @Test
    void 이벤트를_조회할_수_있다() {
        //given
        var organization = createOrganization();
        var member = createMember();
        var loginMember = createLoginMember(member);
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organizationMember, organization);

        //when
        var findEvent = sut.getOrganizationMemberEvent(loginMember, event.getId());

        //then
        assertThat(findEvent).isEqualTo(event);
    }

    @Test
    void 조직에_속해_있지_않으면_이벤트를_조회할_수_없다() {
        //given
        var organization = createOrganization();
        var otherOrganization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(otherOrganization, member);

        //when //then
        assertThatThrownBy(() -> createEvent(organizationMember, organization)).isInstanceOf(
                        UnauthorizedOperationException.class)
                .hasMessage("자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.");
    }

    @Test
    void 이벤트_ID를_이용해_이벤트를_조회할때_해당_이벤트가_없다면_예외가_발생한다() {
        // given
        var member = createMember();
        var loginMember = createLoginMember(member);

        //when //then
        assertThatThrownBy(() -> sut.getOrganizationMemberEvent(loginMember, 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 이벤트 정보입니다.");
    }

    @Test
    void 이벤트_마감_시_조직원_id에_해당하는_조직원이_없다면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organizationMember, organization);
        var now = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> sut.closeEventRegistration(
                event.getId(),
                999L,
                now
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_마감_시_요청한_조직에_소속되지_않았다면_예외가_발생한다() {
        // given
        var organization1 = createOrganization();
        var organization2 = createOrganization();

        var om1Member = createMember("m1", "m1@mail.com");
        var notBelongingOrgMember = createOrganizationMember(organization1, om1Member);

        var om2Member = createMember("m2", "m2@mail.com");
        var hostOrgMember = createOrganizationMember(organization2, om2Member);

        var event = createEvent(hostOrgMember, organization2);
        var now = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> sut.closeEventRegistration(
                event.getId(),
                notBelongingOrgMember.getId(),
                now
        ))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("조직에 소속되지 않은 회원입니다.");
    }

    @Test
    void 주최자는_이벤트를_수동으로_마감할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var orgMember = createOrganizationMember(organization, member);
        var event = createEvent(orgMember, organization);
        var now = LocalDateTime.now();

        // when // then
        assertDoesNotThrow(() -> sut.closeEventRegistration(
                event.getId(),
                orgMember.getId(),
                now.plusDays(1)
                        .plusHours(6)
        ));
    }

    @Test
    void 이벤트_생성_시_조직원에게_알림을_보낸다() {
        // given
        var organization = createOrganization();

        var organizerMember = createMember("organizer", "organizer@mail.com");
        var om1Member = createMember("m1", "m1@mail.com");
        var om2Member = createMember("m2", "m2@mail.com");

        var organizer = createOrganizationMember(organization, organizerMember);
        var om1 = createOrganizationMember(organization, om1Member);
        var om2 = createOrganizationMember(organization, om2Member);

        savePushToken(om1, "token-ng1");
        savePushToken(om2, "token-ng2");

        var now = LocalDateTime.now();
        var request = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5),
                now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(
                        new QuestionCreateRequest("1번 질문", true),
                        new QuestionCreateRequest("2번 질문", false)
                )
        );

        var loginMember = new LoginMember(organizerMember.getId());

        // when
        var savedEvent = sut.createEvent(organization.getId(), loginMember, request, now);

        // then
        var email = EventEmailPayload.of(savedEvent, "새로운 이벤트가 등록되었습니다.");
        var pushPayload = PushNotificationPayload.of(savedEvent, "새로운 이벤트가 등록되었습니다.");

        verify(emailNotifier).sendEmails(List.of("m1@mail.com", "m2@mail.com"), email);
        verify(pushNotifier).sendPushs(List.of("token-ng1", "token-ng2"), pushPayload);
    }

    @Test
    void 이벤트를_수정할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember("organizer", "organizer@email.com");
        var organizationMember = createOrganizationMember(organization, member);

        var now = LocalDateTime.now();
        var event = Event.create(
                "원래 제목",
                "원래 설명",
                "원래 장소",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                "원래 닉네임",
                50
        );
        eventRepository.save(event);

        var updateRequest = new EventUpdateRequest(
                "수정된 제목",
                "수정된 설명",
                "수정된 장소",
                now.plusDays(5),
                now.plusDays(6),
                now.plusDays(7),
                "수정된 닉네임",
                200
        );

        var loginMember = new LoginMember(member.getId());

        // when
        sut.updateEvent(event.getId(), loginMember, updateRequest, now);

        // then
        assertThat(eventRepository.findById(event.getId()))
                .isPresent()
                .hasValueSatisfying(savedEvent -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedEvent.getTitle())
                                .isEqualTo("수정된 제목");
                        softly.assertThat(savedEvent.getDescription())
                                .isEqualTo("수정된 설명");
                        softly.assertThat(savedEvent.getPlace())
                                .isEqualTo("수정된 장소");
                        softly.assertThat(savedEvent.getOrganizerNickname())
                                .isEqualTo("수정된 닉네임");
                        softly.assertThat(savedEvent.getMaxCapacity())
                                .isEqualTo(200);

                        softly.assertThat(savedEvent.getEventOperationPeriod()
                                        .getRegistrationPeriod()
                                        .end())
                                .isEqualTo(now.plusDays(5));
                        softly.assertThat(savedEvent.getEventOperationPeriod()
                                        .getEventPeriod()
                                        .start())
                                .isEqualTo(now.plusDays(6));
                        softly.assertThat(savedEvent.getEventOperationPeriod()
                                        .getEventPeriod()
                                        .end())
                                .isEqualTo(now.plusDays(7));
                    });
                });
    }

    @Test
    void 존재하지_않는_이벤트를_수정하면_예외가_발생한다() {
        // given
        var member = createMember("organizer", "organizer@email.com");
        var loginMember = new LoginMember(member.getId());

        var now = LocalDateTime.now();
        var updateRequest = new EventUpdateRequest(
                "수정된 제목",
                "수정된 설명",
                "수정된 장소",
                now.plusDays(5),
                now.plusDays(6),
                now.plusDays(7),
                "수정된 닉네임",
                200
        );

        // when // then
        assertThatThrownBy(() -> sut.updateEvent(9999L, loginMember, updateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 이벤트 정보입니다.");
    }

    @Test
    void 존재하지_않는_회원으로_수정하면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var organizer = createMember("organizer", "organizer@email.com");
        var organizationMember = createOrganizationMember(organization, organizer);

        var now = LocalDateTime.now();
        var event = Event.create(
                "원래 제목",
                "원래 설명",
                "원래 장소",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                "원래 닉네임",
                50
        );
        eventRepository.save(event);

        var updateRequest = new EventUpdateRequest(
                "수정된 제목",
                "수정된 설명",
                "수정된 장소",
                now.plusDays(5),
                now.plusDays(6),
                now.plusDays(7),
                "수정된 닉네임",
                200
        );
        var loginMember = new LoginMember(9999L);

        // when // then
        assertThatThrownBy(() -> sut.updateEvent(event.getId(), loginMember, updateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_수정_시_게스트들에게_알림을_보낸다() {
        // given
        var organization = createOrganization();
        var organizerMember = createMember("organizer", "organizer@email.com");
        var guestMember1 = createMember("guest1", "guest1@email.com");
        var guestMember2 = createMember("guest2", "guest2@email.com");

        var organizerOrgMember = createOrganizationMember(organization, organizerMember);
        var guestOrgMember1 = createOrganizationMember(organization, guestMember1);
        var guestOrgMember2 = createOrganizationMember(organization, guestMember2);

        savePushToken(guestOrgMember1, "token-ng1");
        savePushToken(guestOrgMember2, "token-ng2");

        var now = LocalDateTime.now();
        var event = Event.create(
                "원래 제목",
                "원래 설명",
                "원래 장소",
                organizerOrgMember,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                "원래 닉네임",
                50
        );
        eventRepository.save(event);

        var guest1 = Guest.create(event, guestOrgMember1, now.plusDays(1));
        var guest2 = Guest.create(event, guestOrgMember2, now.plusDays(1));
        guestRepository.save(guest1);
        guestRepository.save(guest2);

        var updateRequest = new EventUpdateRequest(
                "수정된 제목",
                "수정된 설명",
                "수정된 장소",
                now.plusDays(5),
                now.plusDays(6),
                now.plusDays(7),
                "수정된 닉네임",
                200
        );

        var loginMember = new LoginMember(organizerMember.getId());

        // when
        var updatedEvent = sut.updateEvent(event.getId(), loginMember, updateRequest, now);

        // then
        var email = EventEmailPayload.of(updatedEvent, "이벤트 정보가 수정되었습니다.");
        var pushPayload = PushNotificationPayload.of(updatedEvent, "이벤트 정보가 수정되었습니다.");

        verify(emailNotifier).sendEmails(List.of("guest1@email.com", "guest2@email.com"), email);
        verify(pushNotifier).sendPushs(List.of("token-ng1", "token-ng2"), pushPayload);
    }

    private Organization createOrganization() {
        var organization = Organization.create("우테코", "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private LoginMember createLoginMember(Member member) {
        return new LoginMember(member.getId());
    }

    private Member createMember() {
        return memberRepository.save(Member.create("name", "ahmadda@ahmadda.com"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email));
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        var organizationMember = OrganizationMember.create("surf", member, organization);

        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(final OrganizationMember organizationMember, final Organization organization) {
        var now = LocalDateTime.now();

        var event = Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                "이벤트 근로",
                10
        );

        return eventRepository.save(event);
    }

    private void savePushToken(OrganizationMember organizationMember, String token) {
        var recipient = PushNotificationRecipient.create(organizationMember.getMember(), token);

        pushNotificationRecipientRepository.save(recipient);
    }
}
