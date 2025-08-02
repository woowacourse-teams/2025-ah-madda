package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventTest {

    private Organization baseOrganization;
    private OrganizationMember baseOrganizer;

    @BeforeEach
    void setUp() {
        Member baseMember = createMember("테스트 멤버", "test@example.com");
        baseOrganization = createOrganization();
        baseOrganizer = createOrganizationMember("주최자", baseMember, baseOrganization);
    }

    @Test
    void 이벤트_정보를_수정할_수_있다() {
        // given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(now.plusDays(1), now.plusDays(2));
        var eventPeriod = Period.create(now.plusDays(3), now.plusDays(4));
        var sut = Event.create(
                "이전 제목",
                "이전 설명",
                "이전 장소",
                baseOrganizer,
                baseOrganization,
                EventOperationPeriod.create(registrationPeriod, eventPeriod, now),
                "이전 닉네임",
                10
        );

        var updatedRegistrationPeriod = Period.create(now.plusDays(2), now.plusDays(3));
        var updatedEventPeriod = Period.create(now.plusDays(4), now.plusDays(5));
        var updatedOperationPeriod = EventOperationPeriod.create(updatedRegistrationPeriod, updatedEventPeriod, now);

        // when
        sut.update(
                "수정된 제목",
                "수정된 설명",
                "수정된 장소",
                updatedOperationPeriod,
                "수정된 닉네임",
                20
        );

        // then
        assertSoftly(softly -> {
            softly.assertThat(sut.getTitle())
                    .isEqualTo("수정된 제목");
            softly.assertThat(sut.getDescription())
                    .isEqualTo("수정된 설명");
            softly.assertThat(sut.getPlace())
                    .isEqualTo("수정된 장소");
            softly.assertThat(sut.getEventOperationPeriod())
                    .isEqualTo(updatedOperationPeriod);
            softly.assertThat(sut.getOrganizerNickname())
                    .isEqualTo("수정된 닉네임");
            softly.assertThat(sut.getMaxCapacity())
                    .isEqualTo(20);
        });
    }

    @Test
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2)
        );
        var sut = createEvent(now, registrationPeriod);
        var guest = createOrganizationMember("조직원", createMember("참가자1", "guest1@example.com"), baseOrganization);
        var notGuest = createOrganizationMember("다른 조직원", createMember("참가자2", "guest2@example.com"), baseOrganization);
        Guest.create(sut, guest, registrationPeriod.start());

        // when
        var actual1 = sut.hasGuest(guest);
        var actual2 = sut.hasGuest(notGuest);
        var actual3 = sut.hasGuest(baseOrganizer);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isTrue();
            softly.assertThat(actual2)
                    .isFalse();
            softly.assertThat(actual3)
                    .isTrue();
        });
    }

    @Test
    void 이벤트에_참여한_게스트들을_조회할_수_있다() {
        // given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(now.plusDays(1), now.plusDays(2));
        var sut = createEvent(now, registrationPeriod);

        var guest1 = Guest.create(
                sut,
                createOrganizationMember("게스트1", createMember("게스트1", "g1@email.com"), baseOrganization),
                registrationPeriod.start()
        );
        var guest2 = Guest.create(
                sut,
                createOrganizationMember("게스트2", createMember("게스트2", "g2@email.com"), baseOrganization),
                registrationPeriod.start()
        );

        // when
        var guests = sut.getGuests();

        // then
        assertSoftly(softly -> {
            softly.assertThat(guests)
                    .hasSize(2);
            softly.assertThat(guests)
                    .containsExactlyInAnyOrder(guest1, guest2);
        });
    }

    @Test
    void 주최자는_자신의_조직이_아닌_다른_조직의_이벤트를_생성한다면_예외가_발생한다() {
        //given
        var organization1 = createOrganization("우테코1");
        var organization2 = createOrganization("우테코2");
        var organizationMember = createOrganizationMember(createMember(), organization1);

        //when //then
        assertThatThrownBy(() -> createEvent(organizationMember, organization2))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 2_100_000_001})
    void 이벤트_최대_수용_인원이_1명보다_적거나_21억_보다_클경우_예외가_발생한다(int maxCapacity) {
        assertThatThrownBy(() -> createEvent(
                "title", maxCapacity
        ))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트가_아직_시작되지_않았는지_확인할_수_있다() {
        //given
        var now = LocalDateTime.now();
        var eventOperationPeriod = EventOperationPeriod.create(
                Period.create(now.plusDays(1), now.plusDays(2)),
                Period.create(now.plusDays(3), now.plusDays(4)),
                now
        );
        var event = createEvent("우테코", eventOperationPeriod);

        //when
        var result1 = event.isNotStarted(now);
        var result2 = event.isNotStarted(now.plusDays(3));

        //then
        assertSoftly(softly -> {
            softly.assertThat(result1)
                    .isTrue();
            softly.assertThat(result2)
                    .isFalse();
        });
    }


    @Test
    void 이벤트에_참여하지_않은_조직원을_조회할_수_있다() {
        // given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2)
        );
        var sut = createEvent(now, registrationPeriod);
        var guest = createOrganizationMember("게스트", createMember("게스트", "guest@email.com"), baseOrganization);
        var nonGuest1 = createOrganizationMember("비게스트1", createMember("비게스트1", "non1@email.com"), baseOrganization);
        var nonGuest2 = createOrganizationMember("비게스트2", createMember("비게스트2", "non2@email.com"), baseOrganization);
        Guest.create(sut, guest, registrationPeriod.start());
        var allMembers = List.of(baseOrganizer, guest, nonGuest1, nonGuest2);

        // when
        var nonGuests = sut.getNonGuestOrganizationMembers(allMembers);

        // then
        assertSoftly(softly -> {
            softly.assertThat(nonGuests)
                    .hasSize(2);
            softly.assertThat(nonGuests)
                    .containsExactlyInAnyOrder(nonGuest1, nonGuest2);
        });
    }

    @Test
    void 이벤트에_참여중인_게스트가_또_참여한다면_예외가_발생한다() {
        //given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2)
        );
        var sut = createEvent(now, registrationPeriod);
        var organizationMember =
                createOrganizationMember("게스트", createMember("게스트", "guest@email.com"), baseOrganization);
        var guest = Guest.create(sut, organizationMember, registrationPeriod.start());

        //when //then
        assertThatThrownBy(() -> sut.participate(guest, registrationPeriod.start()))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트에_질문이_포함되어있는지_확인할_수_있다() {
        // given
        var question = Question.create("필수 질문", true, 0);
        var sut = createEvent("이벤트", 10, question);

        var notIncludedQuestion = Question.create("없는 질문", true, 1);
        var otherEvent = createEvent("이벤트2", 10, notIncludedQuestion);

        // when
        var actual1 = sut.hasQuestion(question);
        var actual2 = sut.hasQuestion(notIncludedQuestion);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual1)
                    .isTrue();
            softly.assertThat(actual2)
                    .isFalse();
        });
    }

    @Test
    void 이벤트의_주최자인지_판단한다() {
        // given
        var now = LocalDateTime.now();
        var registrationPeriod = Period.create(
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2)
        );
        var sut = createEvent(now, registrationPeriod);
        var nonOrganizer = createOrganizationMember("다른 조직원", createMember(), baseOrganization);

        // when
        var isOrganizer = sut.isOrganizer(baseOrganizer.getMember());
        var isNotOrganizer = sut.isOrganizer(nonOrganizer.getMember());

        // then
        assertSoftly(softly -> {
            softly.assertThat(isOrganizer)
                    .isTrue();
            softly.assertThat(isNotOrganizer)
                    .isFalse();
        });
    }

    @Test
    void 필수_질문만_조회할_수_있다() {
        // given
        var requiredQuestion = Question.create("필수 질문", true, 0);
        var optionalQuestion = Question.create("선택 질문", false, 1);
        var sut = createEvent("이벤트", 10, requiredQuestion, optionalQuestion);

        // when
        var result = sut.getRequiredQuestions();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(1);
            softly.assertThat(result)
                    .containsExactly(requiredQuestion);
        });
    }

    @Test
    void 이벤트에_참여하지_않았는데_참여_취소시_예외가_발생한다() {
        // given
        var organization = Organization.create("우아한 테크코스", "woowahan-tech-course", "우아한 테크코스 6기");
        var member = Member.create("박미참여", "not.participant.park@woowahan.com");
        var organizationMember =
                OrganizationMember.create("참여안한_조직원", member, organization);


        var member2 = Member.create("김참가", "participant.kim@woowahan.com");
        var organizationMember2 =
                OrganizationMember.create("실제_참가자", member2, organization);

        var sut = createEvent(organizationMember, organization);
        var participate = Guest.create(sut, organizationMember2, sut.getRegistrationStart());

        //when // then
        assertThatThrownBy(() -> sut.cancelParticipation(organizationMember, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트_참여를_취소할_수_있다() {
        // given
        var organization = Organization.create("우아한 테크코스", "woowahan-tech-course", "우아한 테크코스 6기");
        var member = Member.create("박찬양", "creator.chanyang@woowahan.com");
        var organizationMember =
                OrganizationMember.create("이벤트_개설자_닉네임", member, organization);


        var member2 = Member.create("김참가", "participant.kim@woowahan.com");
        var organizationMember2 =
                OrganizationMember.create("참가자A_닉네임", member2, organization);

        var sut = createEvent(organizationMember, organization);
        var participate = Guest.create(sut, organizationMember2, sut.getRegistrationStart());

        //when // then
        assertSoftly(softly -> {
            softly.assertThat(sut.getGuests().size()).isEqualTo(1L);
            sut.cancelParticipation(organizationMember2, LocalDateTime.now());
            softly.assertThat(sut.getGuests().size()).isEqualTo(0L);
        });
    }

    @Test
    void 모집_마감을_할_수_있다() {
        // given
        var yesterday = LocalDateTime.now()
                .minusDays(1);
        var now = LocalDateTime.now();

        var registrationEnd = now.plusDays(1);
        var registrationCloseTime = now.plusHours(6);

        var registrationPeriod = Period.create(
                now,
                registrationEnd
        );
        var sut = createEvent(yesterday, registrationPeriod);

        // when
        sut.closeRegistrationAt(baseOrganizer, registrationCloseTime);

        // then
        Assertions.assertThat(sut.getRegistrationEnd())
                .isEqualTo(registrationCloseTime);
    }

    @Test
    void 모집을_마감하면_게스트가_참여할_수_없다() {
        var yesterday = LocalDateTime.now()
                .minusDays(1);
        var now = LocalDateTime.now();

        var registrationEnd = now.plusDays(1);
        var registrationCloseTime = now.plusHours(6);

        var registrationPeriod = Period.create(
                now,
                registrationEnd
        );

        var sut = createEvent(yesterday, registrationPeriod);

        var organizationMember =
                createOrganizationMember("게스트", createMember("게스트", "guest@email.com"), baseOrganization);

        // when
        sut.closeRegistrationAt(baseOrganizer, registrationCloseTime);

        // then
        assertThatThrownBy(() -> Guest.create(sut, organizationMember, registrationEnd))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 주최자만_이벤트를_마감할_수_있다() {
        // given
        var yesterday = LocalDateTime.now()
                .minusDays(1);
        var now = LocalDateTime.now();

        var registrationEnd = now.plusDays(1);
        var registrationCloseTime = now.plusHours(6);

        var registrationPeriod = Period.create(
                now,
                registrationEnd
        );

        Member member = createMember("주최자 아님", "test1@example.com");
        var notBaseOrganizer = createOrganizationMember("주최자 아님", member, baseOrganization);


        var sut = createEvent(yesterday, registrationPeriod);

        // when // then
        assertThatThrownBy(() -> sut.closeRegistrationAt(notBaseOrganizer, registrationCloseTime))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 마감시간은_등록_종료_시간보다_이전이어야_한다() {
        // given
        var yesterday = LocalDateTime.now()
                .minusDays(1);
        var now = LocalDateTime.now();

        var registrationEnd = now.plusDays(1);
        var registrationCloseOverTime = registrationEnd.plusHours(6);

        var registrationPeriod = Period.create(
                now,
                registrationEnd
        );
        var sut = createEvent(yesterday, registrationPeriod);

        // when // then
        assertThatThrownBy(() -> sut.closeRegistrationAt(baseOrganizer, registrationCloseOverTime))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트_시작_10분_전보다_이전에_참가_취소하면_성공한다() {
        // given
        var now = LocalDateTime.of(2024, 1, 1, 12, 0);
        var eventStartTime = now.plusHours(1);
        var eventPeriod = Period.create(eventStartTime, eventStartTime.plusHours(1));
        var registrationPeriod = Period.create(now.minusDays(1), eventStartTime.minusMinutes(30));
        var eventOperationPeriod = EventOperationPeriod.create(registrationPeriod, eventPeriod, now.minusDays(2));

        var sut = Event.create(
                "이벤트", "설명", "장소",
                baseOrganizer,
                baseOrganization,
                eventOperationPeriod,
                "주최자 닉네임",
                10
        );

        var participantMember = createMember("참가자", "participant@email.com");
        var participant = createOrganizationMember("참가자", participantMember, baseOrganization);
        Guest.create(sut, participant, now.minusDays(1));
        Assertions.assertThat(sut.getGuests()).hasSize(1);

        var cancellationTime = eventStartTime.minusMinutes(11);

        // when
        sut.cancelParticipation(participant, cancellationTime);

        // then
        Assertions.assertThat(sut.getGuests()).isEmpty();
    }

    @Test
    void 이벤트_시작_10분_전_이후에_참가_취소하면_예외가_발생한다() {
        // given
        var now = LocalDateTime.of(2024, 1, 1, 12, 0);
        var eventStartTime = now.plusMinutes(9);
        var eventPeriod = Period.create(eventStartTime, eventStartTime.plusHours(1));
        var registrationPeriod = Period.create(now.minusDays(1), eventStartTime.minusMinutes(5));
        var eventOperationPeriod = EventOperationPeriod.create(registrationPeriod, eventPeriod, now.minusDays(2));

        var sut = Event.create(
                "이벤트", "설명", "장소",
                baseOrganizer,
                baseOrganization,
                eventOperationPeriod,
                "주최자 닉네임",
                10
        );

        var participantMember = createMember("참가자", "participant@email.com");
        var participant = createOrganizationMember("참가자", participantMember, baseOrganization);
        Guest.create(sut, participant, now.minusDays(1));

        // when // then
        assertThatThrownBy(() -> sut.cancelParticipation(participant, now))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트_시작_후에_참가_취소하면_예외가_발생한다() {
        // given
        var now = LocalDateTime.of(2024, 1, 1, 12, 0);
        var eventStartTime = now.minusMinutes(1);
        var eventPeriod = Period.create(eventStartTime, eventStartTime.plusHours(1));
        var registrationPeriod = Period.create(now.minusDays(2), now.minusDays(1));
        var eventOperationPeriod = EventOperationPeriod.create(registrationPeriod, eventPeriod, now.minusDays(3));

        var sut = Event.create(
                "이벤트", "설명", "장소",
                baseOrganizer,
                baseOrganization,
                eventOperationPeriod,
                "주최자 닉네임",
                10
        );

        var participantMember = createMember("참가자", "participant@email.com");
        var participant = createOrganizationMember("참가자", participantMember, baseOrganization);
        Guest.create(sut, participant, now.minusDays(1));

        // when // then
        assertThatThrownBy(() -> sut.cancelParticipation(participant, now))
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    private Event createEvent(String title, int maxCapacity, Question... questions) {
        var organization = createOrganization("우테코");

        return Event.create(
                title,
                "description",
                "place",
                createOrganizationMember(createMember(), organization),
                organization,
                EventOperationPeriod.create(
                        Period.create(
                                LocalDateTime.now()
                                        .plusDays(1),
                                LocalDateTime.now()
                                        .plusDays(2)
                        ),
                        Period.create(
                                LocalDateTime.now()
                                        .plusDays(3),
                                LocalDateTime.now()
                                        .plusDays(4)
                        ),
                        LocalDateTime.now()
                ),
                "이벤트 근로",
                maxCapacity,
                questions
        );
    }

    private Event createEvent(LocalDateTime now, Period registrationPeriod) {
        return Event.create(
                "title",
                "description",
                "place",
                baseOrganizer,
                baseOrganization,
                EventOperationPeriod.create(
                        registrationPeriod,
                        Period.create(now.plusDays(3), now.plusDays(4)),
                        now
                ),
                "이벤트 근로",
                10
        );
    }

    private Member createMember(String name, String email) {
        return Member.create(name, email);
    }

    private Organization createOrganization() {
        return Organization.create("테스트 조직", "설명", "image.png");
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization organization
    ) {
        return OrganizationMember.create(nickname, member, organization);
    }

    private Event createEvent(String title, EventOperationPeriod eventOperationPeriod) {
        var organization = createOrganization("우테코");

        return Event.create(
                title,
                "description",
                "place",
                createOrganizationMember(createMember(), organization),
                organization,
                eventOperationPeriod,
                "이벤트 근로",
                100
        );
    }

    private OrganizationMember createOrganizationMember(Member member, Organization organization) {
        return OrganizationMember.create("nickname", member, organization);
    }

    private Member createMember() {
        return Member.create("이재훈", "dlwogns3413@ahamadda.com");
    }

    private Organization createOrganization(String name) {
        return Organization.create(name, "우테코입니다.", "imageUrl");
    }

    private Event createEvent(OrganizationMember organizationMember, Organization organization) {
        var now = LocalDateTime.now();

        return Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(2)),
                        Period.create(now.plusDays(3), now.plusDays(4)),
                        now
                ),
                "이벤트 근로",
                10
        );
    }
}