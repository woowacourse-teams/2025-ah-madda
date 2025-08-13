package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokeHistoryTest {

    @Mock
    private PushNotifier pushNotifier;

    @Mock
    private PokeHistoryRepository pokeHistoryRepository;

    @Test
    void PokeHistory를_성공적으로_생성한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when
        var pokeHistory = PokeHistory.create(sender, recipient, event, LocalDateTime.now());

        // then
        assertThat(pokeHistory.getSender()).isEqualTo(sender);
        assertThat(pokeHistory.getRecipient()).isEqualTo(recipient);
        assertThat(pokeHistory.getEvent()).isEqualTo(event);
    }

    @Test
    void PokeHistory_생성시_필수_필드가_올바르게_설정된다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());
        var sentAt = LocalDateTime.now();

        // when
        var pokeHistory = PokeHistory.create(sender, recipient, event, sentAt);

        // then
        assertThat(pokeHistory.getSender()).isEqualTo(sender);
        assertThat(pokeHistory.getRecipient()).isEqualTo(recipient);
        assertThat(pokeHistory.getEvent()).isEqualTo(event);
        assertThat(pokeHistory.getSentAt()).isEqualTo(sentAt);
    }

    @Test
    void PokeHistory_생성시_다른_조직원에게_포키를_보낼_수_있다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when
        var pokeHistory = PokeHistory.create(sender, recipient, event, LocalDateTime.now());

        // then
        assertThat(pokeHistory.getSender()).isNotEqualTo(pokeHistory.getRecipient());
        assertThat(pokeHistory.getSender().getOrganization()).isEqualTo(pokeHistory.getRecipient().getOrganization());
    }

    @Test
    void PokeHistory_생성시_이벤트_정보가_올바르게_저장된다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when
        var pokeHistory = PokeHistory.create(sender, recipient, event, LocalDateTime.now());

        // then
        assertThat(pokeHistory.getEvent().getTitle()).isEqualTo("title");
        assertThat(pokeHistory.getEvent().getOrganization()).isEqualTo(organization);
        assertThat(pokeHistory.getEvent().getOrganizer()).isEqualTo(sender);
    }

    @Test
    void PokeHistory_생성시_전송_시간이_올바르게_저장된다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());
        var expectedSentAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        var pokeHistory = PokeHistory.create(sender, recipient, event, expectedSentAt);

        // then
        assertThat(pokeHistory.getSentAt()).isEqualTo(expectedSentAt);
    }

    // 테스트 헬퍼 메서드들
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
