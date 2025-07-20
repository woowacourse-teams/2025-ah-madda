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
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var sut = createEvent();
        var guest = createOrganizationMember("조직원", createMember("참가자1", "guest1@example.com"), baseOrganization);
        var notGuest = createOrganizationMember("다른 조직원", createMember("참가자2", "guest2@example.com"), baseOrganization);
        Guest.create(sut, guest);

        // when
        var actual = sut.hasGuest(guest);
        var unexpected = sut.hasGuest(notGuest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual)
                    .isTrue();
            softly.assertThat(unexpected)
                    .isFalse();
        });
    }

    @Test
    void 이벤트에_참여한_게스트들을_조회할_수_있다() {
        // given
        var sut = createEvent();

        var guest1 = Guest.create(
                sut,
                createOrganizationMember("게스트1", createMember("게스트1", "g1@email.com"), baseOrganization)
        );
        var guest2 = Guest.create(
                sut,
                createOrganizationMember("게스트2", createMember("게스트2", "g2@email.com"), baseOrganization)
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
                new Period(now.plusDays(1), now.plusDays(2)),
                new Period(now.plusDays(3), now.plusDays(4)),
                now
        );
        var event = createEvent("우테코", eventOperationPeriod);

        //when
        var result1 = event.isNotStarted(now);
        var result2 = event.isNotStarted(now.plusDays(3));

        //then
        assertSoftly(softly -> {
            softly.assertThat(result1).isTrue();
            softly.assertThat(result2).isFalse();
        });
    }

    private Event createEvent(final String title, final int maxCapacity) {
        var organization = createOrganization("우테코");

        return Event.create(
                title,
                "description",
                "place",
                createOrganizationMember(createMember(), organization),
                organization,
                EventOperationPeriod.create(
                        new Period(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                        new Period(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4)),
                        LocalDateTime.now()
                ),
                maxCapacity
        );
    }

    @Test
    void 이벤트에_참여하지_않은_조직원을_조회할_수_있다() {
        // given
        var sut = createEvent();
        var guest = createOrganizationMember("게스트", createMember("게스트", "guest@email.com"), baseOrganization);
        var nonGuest1 = createOrganizationMember("비게스트1", createMember("비게스트1", "non1@email.com"), baseOrganization);
        var nonGuest2 = createOrganizationMember("비게스트2", createMember("비게스트2", "non2@email.com"), baseOrganization);
        Guest.create(sut, guest);
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

    private Event createEvent() {
        var now = LocalDateTime.now();

        return Event.create(
                "title",
                "description",
                "place",
                baseOrganizer,
                baseOrganization,
                EventOperationPeriod.create(
                        new Period(now.plusDays(1), now.plusDays(2)),
                        new Period(now.plusDays(3), now.plusDays(4)),
                        now
                ),
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
                        new Period(now.plusDays(1), now.plusDays(2)),
                        new Period(now.plusDays(3), now.plusDays(4)),
                        now
                ),
                10
        );
    }
}
