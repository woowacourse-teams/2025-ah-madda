package com.ahmadda.domain.event;

import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuestTest {

    private Member member;
    private Event event;
    private OrganizationMember participant;
    private OrganizationMember otherParticipant;

    @BeforeEach
    void setUp() {
        var organizerMember = Member.create("주최자 회원", "organizer@example.com", "testPicture");
        var organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        var organizer = OrganizationMember.create("주최자", organizerMember, organization, OrganizationMemberRole.USER);
        var now = LocalDateTime.now();
        event = Event.create(
                "테스트 이벤트", "설명", "장소", organizer, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                50
        );
        member = Member.create("참가자 회원", "guest@example.com", "testPicture");
        participant = OrganizationMember.create("참가자", member, organization, OrganizationMemberRole.USER);
        otherParticipant =
                OrganizationMember.create(
                        "다른 참가자",
                        Member.create("다른 회원", "other@example.com", "testPicture"),
                        organization,
                        OrganizationMemberRole.USER
                );
    }

    @Test
    void 동일한_참가자인지_확인한다() {
        // given
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        // when
        var isSame = guest.isSameOrganizationMember(participant);

        // then
        assertThat(isSame).isTrue();
    }

    @Test
    void 다른_참가자인지_확인한다() {
        // given
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        // when
        var isSame = guest.isSameOrganizationMember(otherParticipant);

        // then
        assertThat(isSame).isFalse();
    }

    @Test
    void 게스트를_생성하면_이벤트에_참여된다() {
        //when
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        //then
        assertThat(event.getGuests()
                .contains(guest)).isTrue();
    }

    @Test
    void 같은_조직이_아닌_이벤트의_구성원이_참여한다면_예외가_발생한다() {
        //given
        var organization1 = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organization2 = Organization.create("테스트 조직2", "조직 설명", "image.png");

        var organizationMember1 =
                OrganizationMember.create("테스트 닉네임", member, organization1, OrganizationMemberRole.USER);
        var organizationMember2 =
                OrganizationMember.create("테스트 닉네임", member, organization2, OrganizationMemberRole.USER);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember1, organization1,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                50
        );

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember2, event.getRegistrationStart()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("같은 조직의 이벤트에만 게스트로 참여가능합니다.");
    }

    @Test
    void 이벤트의_주최자가_게스트가_된다면_예외가_발생한다() {
        //given
        var organization = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organizationMember =
                OrganizationMember.create("테스트 닉네임", member, organization, OrganizationMemberRole.USER);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                50
        );

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember, event.getRegistrationStart()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이벤트의 주최자는 게스트로 참여할 수 없습니다.");
    }

    @Test
    void 이벤트_수용인원이_가득찼다면_게스트를_생성할_경우_예외가_발생한다() {
        //given
        var organization = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organizationMember1 =
                OrganizationMember.create("테스트 닉네임1", member, organization, OrganizationMemberRole.USER);
        var organizationMember2 =
                OrganizationMember.create("테스트 닉네임2", member, organization, OrganizationMemberRole.USER);
        var organizationMember3 =
                OrganizationMember.create("테스트 닉네임3", member, organization, OrganizationMemberRole.USER);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember1, organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(5),
                        now.plusDays(10), now.plusDays(11),
                        now
                ),
                1
        );
        Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember3, event.getRegistrationStart()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("수용 인원이 가득차 이벤트에 참여할 수 없습니다.");
    }

    @Test
    void 이벤트_신청_기간이_아니라면_게스트_생성시_예외가_발생한다() {
        assertThatThrownBy(() -> Guest.create(
                event,
                participant,
                event.getRegistrationStart()
                        .minusDays(1)
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이벤트 신청은 신청 시작 시간부터 신청 마감 시간까지 가능합니다.");
    }

    @Test
    void 필수_질문에_대한_답변이_모두_있다면_정상적으로_답변이_등록된다() {
        // given
        var now = LocalDateTime.now();

        var question1 = Question.create("필수 질문1", true, 0);
        var question2 = Question.create("선택 질문2", false, 1);
        var event = createEvent("이벤트", participant, now, question1, question2);

        var guest = Guest.create(event, otherParticipant, now);

        var answers = Map.of(
                question1, "답변1",
                question2, "답변2"
        );

        // when
        guest.submitAnswers(answers);

        // then
        assertThat(guest.getAnswers()).hasSize(2);
        assertThat(guest.getAnswers())
                .extracting(Answer::getAnswerText)
                .containsExactlyInAnyOrder("답변1", "답변2");
    }

    @Test
    void 필수_질문에_대한_답변이_누락되면_예외가_발생한다() {
        // given
        var now = LocalDateTime.now();

        var question1 = Question.create("필수 질문1", true, 0);
        var question2 = Question.create("선택 질문2", false, 1);
        var event = createEvent("이벤트", participant, now, question1, question2);

        var guest = Guest.create(event, otherParticipant, now);

        var answers = Map.of(
                question2, "답변2"
        );

        // when // then
        assertThatThrownBy(() -> guest.submitAnswers(answers))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("필수 질문에 대한 답변이 누락되었습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void 필수_질문에_대한_답변이_빈_문자열값_이라면_예외가_발생한다(String answer) {
        // given
        var now = LocalDateTime.now();

        var question = Question.create("필수 질문1", true, 0);
        var event = createEvent("이벤트", participant, now, question);

        var guest = Guest.create(event, otherParticipant, now);

        var answers = Map.of(
                question, answer
        );

        // when // then
        assertThatThrownBy(() -> guest.submitAnswers(answers))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("필수 질문에 대한 답변이 누락되었습니다");
    }

    @Test
    void 이벤트에_없는_질문에_답변하면_예외가_발생한다() {
        // given
        var now = LocalDateTime.now();

        var event = createEvent("이벤트", participant, now);
        var guest = Guest.create(event, otherParticipant, now);

        var externalQuestion = Question.create("외부 질문", true, 0);
        var otherEvent = createEvent("다른 이벤트", participant, now, externalQuestion);

        var answers = Map.of(
                externalQuestion, "외부 답변"
        );

        // when // then
        assertThatThrownBy(() -> guest.submitAnswers(answers))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("이벤트에 포함되지 않은 질문입니다");
    }

    @Test
    void 게스트의_답변을_주최자가_볼_수_있다() {
        // given
        var now = LocalDateTime.now();
        var question1 = Question.create("질문1", true, 0);
        var question2 = Question.create("질문2", false, 1);
        var event = createEvent("이벤트", participant, now, question1, question2);

        var guest = Guest.create(event, otherParticipant, now);
        var answers = Map.of(
                question1, "답변1",
                question2, "답변2"
        );
        guest.submitAnswers(answers);

        // when
        var retrievedAnswers = guest.viewAnswersAs(participant);

        // then
        assertThat(retrievedAnswers).hasSize(2);
        assertThat(retrievedAnswers)
                .extracting(Answer::getAnswerText)
                .containsExactlyInAnyOrder("답변1", "답변2");
    }

    @Test
    void 게스트_본인은_자신의_답변을_볼_수_있다() {
        // given
        var now = LocalDateTime.now();
        var question = Question.create("질문", true, 0);
        var event = createEvent("이벤트", participant, now, question);
        var guest = Guest.create(event, otherParticipant, now);

        var answers = Map.of(question, "답변");
        guest.submitAnswers(answers);

        // when
        var retrievedAnswers = guest.viewAnswersAs(otherParticipant);

        // then
        assertThat(retrievedAnswers).hasSize(1);
        assertThat(retrievedAnswers.get(0)
                .getAnswerText()).isEqualTo("답변");
    }

    @Test
    void 게스트의_답변을_조회할_권한이_없으면_예외가_발생한다() {
        // given
        var now = LocalDateTime.now();
        var question = Question.create("질문", true, 0);
        var event = createEvent("이벤트", participant, now, question);
        var otherMember = Member.create("다른 회원", "email@email.com", "profileUrl");
        var othrerOrganizationMember =
                OrganizationMember.create(
                        "다른 게스트",
                        otherMember,
                        participant.getOrganization(),
                        OrganizationMemberRole.USER
                );
        var guest = Guest.create(event, otherParticipant, now);
        var answers = Map.of(question, "답변");
        guest.submitAnswers(answers);

        // when // then
        assertThatThrownBy(() -> guest.viewAnswersAs(othrerOrganizationMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("답변을 볼 권한이 없습니다.");
    }

    private Event createEvent(
            String title,
            OrganizationMember organizer,
            LocalDateTime now,
            Question... questions
    ) {
        return Event.create(
                title, "설명", "장소", organizer,
                organizer.getOrganization(),
                EventOperationPeriod.create(
                        now, now.plusDays(1),
                        now.plusDays(2), now.plusDays(3),
                        now
                ),
                10,
                questions
        );
    }
}
