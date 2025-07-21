package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        var organizerMember = Member.create("주최자 멤버", "organizer@example.com");
        var organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        var organizer = OrganizationMember.create("주최자", organizerMember, organization);
        var now = LocalDateTime.now();
        event = Event.create(
                "테스트 이벤트", "설명", "장소", organizer, organization,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(5)),
                        Period.create(now.plusDays(10), now.plusDays(11)),
                        now
                ),
                50
        );
        member = Member.create("참가자 멤버", "guest@example.com");
        participant = OrganizationMember.create("참가자", member, organization);
        otherParticipant =
                OrganizationMember.create("다른 참가자", Member.create("다른 멤버", "other@example.com"), organization);
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
    void 같은_조직이_아닌_이벤트의_조직원이_참여한다면_예외가_발생한다() {
        //given
        var organization1 = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organization2 = Organization.create("테스트 조직2", "조직 설명", "image.png");

        var organizationMember1 = OrganizationMember.create("테스트 닉네임", member, organization1);
        var organizationMember2 = OrganizationMember.create("테스트 닉네임", member, organization2);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember1, organization1,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(5)),
                        Period.create(now.plusDays(10), now.plusDays(11)),
                        now
                ),
                50
        );

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember2, event.getRegistrationStart()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("같은 조직의 이벤트에만 게스트로 참여가능합니다.");
    }

    @Test
    void 이벤트의_주최자가_게스트가_된다면_예외가_발생한다() {
        //given
        var organization = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organizationMember = OrganizationMember.create("테스트 닉네임", member, organization);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember, organization,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(5)),
                        Period.create(now.plusDays(10), now.plusDays(11)),
                        now
                ),
                50
        );

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember, event.getRegistrationStart()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트의 주최자는 게스트로 참여할 수 없습니다.");
    }

    @Test
    void 이벤트_수용인원이_가득찼다면_게스트를_생성할_경우_예외가_발생한다() {
        //given
        var organization = Organization.create("테스트 조직1", "조직 설명", "image.png");
        var organizationMember1 = OrganizationMember.create("테스트 닉네임1", member, organization);
        var organizationMember2 = OrganizationMember.create("테스트 닉네임2", member, organization);
        var organizationMember3 = OrganizationMember.create("테스트 닉네임3", member, organization);

        var now = LocalDateTime.now();
        var event = Event.create(
                "테스트 이벤트", "설명", "장소", organizationMember1, organization,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(5)),
                        Period.create(now.plusDays(10), now.plusDays(11)),
                        now
                ),
                1
        );
        Guest.create(event, organizationMember2, event.getRegistrationStart());

        //when //then
        assertThatThrownBy(() -> Guest.create(event, organizationMember3, event.getRegistrationStart()))
                .isInstanceOf(BusinessRuleViolatedException.class)
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
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 신청 기간이 아닙니다.");
    }

    @Test
    void 필수_질문에_대한_답변이_모두_있다면_정상적으로_답변이_등록된다() {
        // given
        var now = LocalDateTime.now();
        var event = createEvent("이벤트", participant, now);

        var question1 = Question.create(event, "필수 질문1", true, 0);
        var question2 = Question.create(event, "선택 질문2", false, 1);

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
        var event = createEvent("이벤트", participant, now);

        var question1 = Question.create(event, "필수 질문1", true, 0);
        var question2 = Question.create(event, "선택 질문2", false, 1);

        var guest = Guest.create(event, otherParticipant, now);

        var answers = Map.of(
                question2, "답변2"
        );

        // when // then
        assertThatThrownBy(() -> guest.submitAnswers(answers))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessageContaining("필수 질문에 대한 답변이 누락되었습니다");
    }

    @Test
    void 이벤트에_없는_질문에_답변하면_예외가_발생한다() {
        // given
        var now = LocalDateTime.now();

        var event = createEvent("이벤트", participant, now);
        var guest = Guest.create(event, otherParticipant, now);

        var otherEvent = createEvent("다른 이벤트", participant, now);
        var externalQuestion = Question.create(otherEvent, "외부 질문", true, 0);

        var answers = Map.of(
                externalQuestion, "외부 답변"
        );

        // when // then
        assertThatThrownBy(() -> guest.submitAnswers(answers))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessageContaining("이벤트에 포함되지 않은 질문입니다");
    }

    private Event createEvent(
            String title,
            OrganizationMember organizer,
            LocalDateTime now
    ) {
        return Event.create(
                title, "설명", "장소", organizer,
                organizer.getOrganization(),
                EventOperationPeriod.create(
                        Period.create(now, now.plusDays(1)),
                        Period.create(now.plusDays(2), now.plusDays(3)),
                        now
                ),
                10
        );
    }
}
