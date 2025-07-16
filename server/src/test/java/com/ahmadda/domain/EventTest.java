package com.ahmadda.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class EventTest {

    private Member member;
    private Organization organization;
    private OrganizationMember organizer;

    @BeforeEach
    void setUp() {
        member = Member.create("테스트 멤버", "test@example.com");
        organization = Organization.create("테스트 조직", "조직 설명", "image.png");
        organizer = OrganizationMember.create("주최자", member, organization);
    }

    @Test
    void 게스트가_이벤트에_참여했는지_알_수_있다() {
        // given
        var event = createEvent("event");
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
        assertThatThrownBy(() -> createEvent(title))
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
                .isInstanceOf(BusinessRuleViolatedException.class);
    }

    @Test
    void 이벤트_시작_시간이_이벤트_생성_요청_시점보다_과거라면_예외가_발생한다() {
        //given
        var eventCreateRequestDateTime = LocalDateTime.of(2025, 7, 16, 10, 0);
        var eventStartDateTime = LocalDateTime.of(2025, 7, 16, 9, 0);
        var eventEndDateTime = LocalDateTime.of(2025, 7, 16, 14, 0);

        //when //then
        assertThatThrownBy(() -> createEvent(eventStartDateTime, eventEndDateTime, eventCreateRequestDateTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 시작 시간은 이벤트 생성 요청 시점보다 과거일 수 없습니다.");
    }

    @ParameterizedTest
    @CsvSource({"2025-07-16T08:59", "2025-07-16T09:00"})
    void 이벤트_종료_시간이_이벤트_시작_시간보다_과거_이거나_같다면_예외가_발생한다(LocalDateTime eventEndDateTime) {
        //given
        var eventCreateRequestDateTime = LocalDateTime.of(2025, 7, 16, 8, 0);
        var eventStartDateTime = LocalDateTime.of(2025, 7, 16, 9, 0);

        //when //then
        assertThatThrownBy(() -> createEvent(eventStartDateTime, eventEndDateTime, eventCreateRequestDateTime))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이벤트 종료 시간은 이벤트 시작 시간보다 과거 이거나 같을 수 없습니다.");
    }

    private Event createEvent(final String title) {
        return Event.create(
                title,
                "description",
                "place",
                createOrganizationMember(createMember(), createOrganization("우테코")),
                createOrganization("우테코"),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10,
                LocalDateTime.now()
        );
    }

    private Event createEvent(final LocalDateTime eventStart,
                              final LocalDateTime eventEnd,
                              final LocalDateTime currentDateTime) {
        Organization organization = createOrganization("우테코");
        return Event.create(
                "title",
                "description",
                "place",
                createOrganizationMember(createMember(), organization),
                organization,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                eventStart,
                eventEnd,
                10,
                currentDateTime
        );
    }

    private Event createEvent(final OrganizationMember organizationMember, final Organization organization) {
        return Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10,
                LocalDateTime.now()
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
