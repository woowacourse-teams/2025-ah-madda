package com.ahmadda.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.ahmadda.domain.exception.UnauthorizedOperationException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class EventStatisticTest {

    @Test
    void 이벤트_주최자가_아니라면_조회수_요청시_예외를_반환한다() {
        //given
        var organization = createOrganization("우테코1");
        var organizationMember = createOrganizationMember(createMember("서프", "surf@gmail.com"), organization);
        var notOrganizerOrganizationMember = createOrganizationMember(createMember("투다", "tuda@gmail.com"), organization);
        var event = createEvent(organizationMember, organization);

        //when
        var sut = EventStatistic.create(event, LocalDateTime.now());

        //then
        assertThatThrownBy(() -> sut.getEventViewMatrics(notOrganizerOrganizationMember, LocalDateTime.now()))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessage("이벤트의 조회수는 이벤트의 주최자만 참조할 수 있습니다.");
    }

    @Test
    void 이벤트종료일_당일인_경우_이벤트_종료일까지의_조회수를_가져올_수_있다() {
        //given
        var organization = createOrganization("우테코1");
        var organizationMember = createOrganizationMember(createMember("서프", "surf@gmail.com"), organization);
        var event = createEvent(organizationMember, organization);

        var startLocalDateTime = event.getEventOperationPeriod().getRegistrationPeriod().start();
        var endLocalDateTime = event.getEventOperationPeriod().getEventPeriod().end();
        var eventDuration = ChronoUnit.DAYS
                .between(startLocalDateTime, endLocalDateTime);

        //when
        var sut = EventStatistic.create(event, startLocalDateTime);

        //then
        assertThat(sut.getEventViewMatrics(organizationMember, endLocalDateTime)
                .size())
                .isEqualTo(eventDuration);
    }

    @Test
    void 이벤트가_끝나지_않은_경우_오늘까지의_조회수만_반환한다() {
        //given
        var organization = createOrganization("우테코1");
        var organizationMember = createOrganizationMember(createMember("서프", "surf@gmail.com"), organization);
        var event = createEvent(organizationMember, organization);

        LocalDateTime beforeEventEndDatetime = event.getEventEnd()
                .minusDays(1L);

        //when
        var sut = EventStatistic.create(event, beforeEventEndDatetime);

        //then
        assertThat(sut.getEventViewMatrics(organizationMember, beforeEventEndDatetime)
                .getFirst()
                .getViewDate())
                .isEqualTo(beforeEventEndDatetime);
    }


    @Test
    void 이벤트_종료일_이후에는_이벤트_종료일까지의_조회수만_반환한다() {
        //given
        var organization = createOrganization("우테코1");
        var organizationMember = createOrganizationMember(createMember("서프", "surf@gmail.com"), organization);
        var event = createEvent(organizationMember, organization);

        var startLocalDateTime = event.getEventOperationPeriod().getRegistrationPeriod().start();
        var endLocalDateTime = event.getEventOperationPeriod().getEventPeriod().end();
        var eventDuration = ChronoUnit.DAYS
                .between(startLocalDateTime, endLocalDateTime);

        //when
        var sut = EventStatistic.create(event, startLocalDateTime);

        //then
        assertThat(sut.getEventViewMatrics(organizationMember, LocalDateTime.MAX)
                .size())
                .isEqualTo(eventDuration);
    }

    @Test
    void 오늘_날짜의_조회수를_증가시킬_수_있다() {

    }

    @Test
    void 이벤트_종료_이후_날짜의_조회수를_증가시키려하면_예외가_발생한다() {

    }

    @Test
    void 이벤트_마감시작_전_날짜의_조회수를_증가시키려하면_예외가_발생한다() {

    }

    private OrganizationMember createOrganizationMember(Member member, Organization organization) {
        return OrganizationMember.create("nickname", member, organization);
    }

    private Member createMember(String name, String email) {
        return Member.create(name, email);
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
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                "이벤트 근로",
                10
        );
    }
}
