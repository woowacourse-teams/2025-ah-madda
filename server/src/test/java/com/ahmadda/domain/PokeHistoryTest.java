package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PokeHistoryTest {

    @Test
    void 스스로에게_포키를_보낼_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> PokeHistory.create(sender, sender, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("스스로에게 포키를 보낼 수 없습니다");
    }

    @Test
    void 주최자에게_포키를_보낼_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var organizerMember = createMember("organizer");
        var organizer = createOrganizationMember(organization, organizerMember);
        var event = createEvent(organization, organizer, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> PokeHistory.create(sender, organizer, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("주최자에게 포키를 보낼 수 없습니다");
    }

    @Test
    void 이미_이벤트에_참여한_조직원에게_포키를_보낼_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var organizerMember = createMember("organizer");

        var otherMember = createMember("otherMember");
        var otherOrganizationMember = createOrganizationMember(organization, otherMember);
        var organizer = createOrganizationMember(organization, organizerMember);
        var event = createEvent(organization, organizer, LocalDateTime.now());

        event.participate(Guest.create(event, otherOrganizationMember, LocalDateTime.now()), LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> PokeHistory.create(sender, otherOrganizationMember, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("이미 이벤트에 참여한 조직원에게 포키를 보낼 수 없습니다.");
    }

    @Test
    void 보내는_사람이_조직에_참여하고_있지_않을_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var anotherOrganization = createOrganization("another");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(anotherOrganization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, recipient, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> PokeHistory.create(sender, recipient, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("포키를 보내려면 해당 조직에 참여하고 있어야 합니다.");
    }

    @Test
    void 받는_사람이_조직에_참여하고_있지_않을_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var anotherOrganization = createOrganization("another");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(anotherOrganization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> PokeHistory.create(sender, recipient, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("포키 대상이 해당 조직에 참여하고 있어야 합니다.");
    }


    private Organization createOrganization(String name) {
        return Organization.create(name, "http://image.com", "설명");
    }

    private Member createMember(String name) {
        return Member.create(name, "test@email.com", "http://picture.com");
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        return OrganizationMember.create("nickname", member, organization);
    }

    private Event createEvent(Organization organization, OrganizationMember organizer, LocalDateTime now) {
        return Event.create(
                "title",
                "content",
                "place",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.plusDays(1), now.plusDays(2),
                        now.plusDays(3), now.plusDays(4),
                        now
                ),
                20,
                List.of()
        );
    }
}
