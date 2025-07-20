package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EventTest {

    private Organization organization = Organization.create("테스트 조직", "조직 설명", "image.png");

    @Test
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var event = createEvent("event", 10);
        var member1 = Member.create("참가자1", "guest1@example.com");
        var member2 = Member.create("참가자2", "guest2@example.com");
        var organizationMember = OrganizationMember.create("조직원", member1, organization);
        var anotherOrganizationMember = OrganizationMember.create("다른 조직원", member2, organization);
        event.getGuests().add(Guest.create(event, organizationMember));

        // when
        var actual = event.hasGuest(organizationMember);
        var unexpected = event.hasGuest(anotherOrganizationMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual).isTrue();
            softly.assertThat(unexpected).isFalse();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void 이벤트_제목이_비어있다면_예외가_발생한다(String title) {
        assertThatThrownBy(() -> createEvent(title, 10))
                .isInstanceOf(BusinessRuleViolatedException.class);
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

    private OrganizationMember createOrganizationMember(final Member member, final Organization organization) {
        return OrganizationMember.create("nickname", member, organization);
    }

    private Member createMember() {
        return Member.create("이재훈", "dlwogns3413@ahamadda.com");
    }

    private Organization createOrganization(final String name) {
        return Organization.create(name, "우테코입니다.", "imageUrl");
    }
}
