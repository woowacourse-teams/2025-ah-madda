package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
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
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("자신이 속한 조직에서만 이벤트를 생성할 수 있습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 2_100_000_001})
    void 이벤트_최대_수용_인원이_1명보다_적거나_21억_보다_클경우_예외가_발생한다(int maxCapacity) {
        assertThatThrownBy(() -> createEvent(
                "title", maxCapacity
        ))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("최대 수용 인원은 1명보다 적거나 21억명 보다 클 수 없습니다.");
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
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이미 해당 이벤트에 참여중인 게스트입니다.");
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

    private Event createEvent(final String title, final int maxCapacity, Question... questions) {
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

    private Event createEvent(final String title, EventOperationPeriod eventOperationPeriod) {
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

    private OrganizationMember createOrganizationMember(final Member member, final Organization organization) {
        return OrganizationMember.create("nickname", member, organization);
    }

    private Member createMember() {
        return Member.create("이재훈", "dlwogns3413@ahamadda.com");
    }

    private Organization createOrganization(final String name) {
        return Organization.create(name, "우테코입니다.", "imageUrl");
    }

    private Event createEvent(final OrganizationMember organizationMember, final Organization organization) {
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
