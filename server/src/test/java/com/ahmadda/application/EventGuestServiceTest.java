package com.ahmadda.application;

import com.ahmadda.application.dto.AnswerCreateRequest;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Answer;
import com.ahmadda.domain.Event;
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
import com.ahmadda.domain.Period;
import com.ahmadda.domain.Question;
import com.ahmadda.domain.QuestionRepository;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventGuestServiceTest {

    @Autowired
    private EventGuestService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void 이벤트에_참여한_게스트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization();
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), organization);
        var event = createAndSaveEvent(organizer, organization);

        var guest1 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember("게스트1", createAndSaveMember("게스트1", "g1@email.com"), organization)
        );
        var guest2 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember("게스트2", createAndSaveMember("게스트2", "g2@email.com"), organization)
        );

        // when
        var result = sut.getGuests(event.getId(), createLoginMember(organizer));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(guest1, guest2);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_게스트_조회시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("주최자", "host@email.com"), organization);
        var loginMember = createLoginMember(organizer);

        // when // then
        assertThatThrownBy(() -> sut.getGuests(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 주최자가_아닌_회원이_게스트_조회시_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), organization);
        var otherMember =
                createAndSaveOrganizationMember("다른사람", createAndSaveMember("user", "user@email.com"), organization);
        var event = createAndSaveEvent(organizer, organization);

        // when // then
        assertThatThrownBy(() -> sut.getGuests(event.getId(), createLoginMember(otherMember)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("이벤트 주최자가 아닙니다.");
    }

    @Test
    void 이벤트에_참여하지_않은_비게스트_조직원들을_조회한다() {
        // given
        var org = createAndSaveOrganization();
        var organizer = createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), org);
        var event = createAndSaveEvent(organizer, org);

        var guest = createAndSaveOrganizationMember("게스트", createAndSaveMember("게스트", "g@email.com"), org);
        var nonGuest1 = createAndSaveOrganizationMember("비게스트1", createAndSaveMember("비게스트1", "ng1@email.com"), org);
        var nonGuest2 = createAndSaveOrganizationMember("비게스트2", createAndSaveMember("비게스트2", "ng2@email.com"), org);
        createAndSaveGuest(event, guest);

        // when
        var result = sut.getNonGuestOrganizationMembers(event.getId(), createLoginMember(organizer));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(nonGuest1, nonGuest2);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_비게스트_조회시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("조직명", "설명", "img.png"));
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("주최자", "host@email.com"), organization);
        var loginMember = createLoginMember(organizer);

        // when // then
        assertThatThrownBy(() -> sut.getNonGuestOrganizationMembers(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 주최자가_아닌_회원이_비게스트_조회시_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), organization);
        var otherMember =
                createAndSaveOrganizationMember("다른사람", createAndSaveMember("user", "user@email.com"), organization);
        var event = createAndSaveEvent(organizer, organization);

        // when // then
        assertThatThrownBy(() -> sut.getNonGuestOrganizationMembers(event.getId(), createLoginMember(otherMember)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("이벤트 주최자가 아닙니다.");
    }

    @Test
    void 조직원은_이벤트의_게스트로_참여할_수_있다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var organizationMember1 = createAndSaveOrganizationMember("surf1", member1, organization);
        var organizationMember2 = createAndSaveOrganizationMember("surf2", member2, organization);
        var event = createAndSaveEvent(organizationMember1, organization);

        // when
        sut.participantEvent(
                event.getId(),
                member2.getId(),
                event.getRegistrationStart(),
                new EventParticipateRequest(List.of())
        );

        // then
        var guests = guestRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(guests)
                    .hasSize(1);
            var guest = guests.getFirst();
            softly.assertThat(guest.getEvent())
                    .isEqualTo(event);
            softly.assertThat(guest.getOrganizationMember())
                    .isEqualTo(organizationMember2);
        });
    }

    @Test
    void 필수_질문에_모두_답변하면_참여할_수_있다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization);
        var participant = createAndSaveOrganizationMember("participant", member2, organization);
        var event = createAndSaveEvent(organizer, organization);

        var question1 = createAndSaveQuestion(event, "필수 질문", true, 0);
        var question2 = createAndSaveQuestion(event, "선택 질문", false, 1);
        event.addQuestions(question1, question2);

        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(question1.getId(), "답변1"),
                new AnswerCreateRequest(question2.getId(), "답변2")
        ));

        // when
        sut.participantEvent(event.getId(), member2.getId(), event.getRegistrationStart(), request);

        // then
        var guest = guestRepository.findAll()
                .getFirst();
        assertSoftly(softly -> {
            softly.assertThat(guest.getAnswers())
                    .hasSize(2);
            softly.assertThat(guest.getAnswers())
                    .extracting(Answer::getAnswerText)
                    .containsExactlyInAnyOrder("답변1", "답변2");
        });
    }

    @Test
    void 필수_질문에_대한_답변이_누락되면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization);
        var participant = createAndSaveOrganizationMember("participant", member2, organization);
        var event = createAndSaveEvent(organizer, organization);

        var question1 = createAndSaveQuestion(event, "필수 질문", true, 0);
        var question2 = createAndSaveQuestion(event, "선택 질문", false, 1);
        event.addQuestions(question1, question2);

        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(question2.getId(), "선택 답변")
        ));

        // when // then
        assertThatThrownBy(() ->
                sut.participantEvent(event.getId(), member2.getId(), event.getRegistrationStart(), request)
        )
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessageContaining("필수 질문에 대한 답변이 누락되었습니다");
    }

    @Test
    void 존재하지_않는_질문에_답변하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization);
        var participant = createAndSaveOrganizationMember("participant", member2, organization);
        var event = createAndSaveEvent(organizer, organization);

        var invalidQuestionId = 999L;

        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(invalidQuestionId, "답변")
        ));

        // when // then
        assertThatThrownBy(() ->
                sut.participantEvent(event.getId(), member2.getId(), event.getRegistrationStart(), request)
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 질문입니다.");
    }

    private Member createAndSaveMember(String name, String email) {
        return memberRepository.save(Member.create(name, email));
    }

    private Organization createAndSaveOrganization() {
        return organizationRepository.save(Organization.create("조직", "설명", "img.png"));
    }

    private OrganizationMember createAndSaveOrganizationMember(String nickname, Member member, Organization org) {
        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, org));
    }

    private Event createAndSaveEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        Period.create(now.minusDays(3), now.minusDays(1)),
                        Period.create(now.plusDays(1), now.plusDays(2)),
                        now.minusDays(6)
                ),
                organizer.getNickname(),
                100
        );

        return eventRepository.save(event);
    }

    private Guest createAndSaveGuest(Event event, OrganizationMember member) {
        return guestRepository.save(Guest.create(event, member, event.getRegistrationStart()));
    }

    private Question createAndSaveQuestion(Event event, String text, boolean required, int order) {
        return questionRepository.save(Question.create(event, text, required, order));
    }

    private LoginMember createLoginMember(OrganizationMember organizationMember) {
        var member = organizationMember.getMember();

        return new LoginMember(member.getId(), member.getName(), member.getEmail());
    }
}
