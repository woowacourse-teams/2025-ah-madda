package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class PokeTest {

    @Autowired
    private Poke sut;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PokeHistoryRepository pokeHistoryRepository;

    @MockitoBean
    private PushNotifier pushNotifier;

    @Test
    void 포키를_성공적으로_전송한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());
        var sentAt = LocalDateTime.now();

        // when
        sut.doPoke(sender, recipient, event, sentAt);

        // then
        verify(pushNotifier).sendPush(eq(recipient), argThat(Objects::nonNull));
    }

    @Test
    void 스스로에게_포키를_보낼_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> sut.doPoke(sender, sender, event, LocalDateTime.now()))
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
        assertThatThrownBy(() -> sut.doPoke(sender, organizer, event, LocalDateTime.now()))
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
        var organizer = createOrganizationMember(organization, organizerMember);
        var event = createEvent(organization, organizer, LocalDateTime.now());

        var otherMember = createMember("otherMember");
        var otherOrganizationMember = createOrganizationMember(organization, otherMember);
        var guest = Guest.create(
                event,
                otherOrganizationMember,
                LocalDateTime.now()
                        .plusDays(1)
        );

        // when // then
        assertThatThrownBy(() -> sut.doPoke(sender, otherOrganizationMember, event, LocalDateTime.now()))
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
        assertThatThrownBy(() -> sut.doPoke(sender, recipient, event, LocalDateTime.now()))
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
        assertThatThrownBy(() -> sut.doPoke(sender, recipient, event, LocalDateTime.now()))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("포키 대상이 해당 조직에 참여하고 있어야 합니다.");
    }

    @Test
    void 포키_30분내_10번_전송_횟수_제한을_초과할_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());
        var sentAt = LocalDateTime.now();

        var firstSentAt = sentAt;
        for (int i = 1; i <= 10; i++) {
            if (i == 1) {
                firstSentAt = sentAt.plusMinutes(i);
            }
            var pokeHistory = PokeHistory.create(sender, recipient, event, sentAt.plusMinutes(i));
            pokeHistoryRepository.save(pokeHistory);
        }

        var duplicateCheckStart = sentAt.minusMinutes(30);
        var expectWaitingMinutes = ChronoUnit.MINUTES.between(duplicateCheckStart, firstSentAt);

        // when // then
        assertThatThrownBy(() -> sut.doPoke(sender, recipient, event, sentAt))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage(String.format(
                        "%s님에게 너무 많은 포키를 보냈어요. %d분 뒤에 다시 요청해주세요.",
                        recipient.getNickname(),
                        expectWaitingMinutes
                ));
    }

    @Test
    void 포키_전송_날짜가_null일_때_예외가_발생한다() {
        // given
        var organization = createOrganization("ahmadda");
        var senderMember = createMember("sender");
        var sender = createOrganizationMember(organization, senderMember);
        var recipientMember = createMember("recipient");
        var recipient = createOrganizationMember(organization, recipientMember);
        var event = createEvent(organization, sender, LocalDateTime.now());

        // when // then
        assertThatThrownBy(() -> sut.doPoke(sender, recipient, event, null))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("포키 전송 날짜는 null 일 수 없습니다.");
    }

    private Organization createOrganization(String name) {
        var organization = Organization.create(name, "설명", "http://image.com");
        return organizationRepository.save(organization);
    }

    private Member createMember(String name) {
        var member = Member.create(name, name + "@email.com", "http://picture.com");
        return memberRepository.save(member);
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        if (organization.getId() == null) {
            organization = organizationRepository.save(organization);
        }
        if (member.getId() == null) {
            member = memberRepository.save(member);
        }

        var organizationMember = OrganizationMember.create("nickname", member, organization);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(Organization organization, OrganizationMember organizer, LocalDateTime now) {
        var event = Event.create(
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
        return eventRepository.save(event);
    }
}
