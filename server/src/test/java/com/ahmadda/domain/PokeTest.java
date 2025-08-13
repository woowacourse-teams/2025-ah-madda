package com.ahmadda.domain;

import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class PokeTest {

    @Autowired
    private Poke sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private PokeHistoryRepository pokeHistoryRepository;

    @MockitoBean
    private PushNotifier pushNotifier;

    @Test
    void 포키를_30분내_한_대상에게_10번_초과하여_요청시_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var sender = createAndSaveOrganizationMember(
                "보내는사람",
                createAndSaveMember("보내는사람", "sender@email.com"),
                organization
        );
        var recipient = createAndSaveOrganizationMember(
                "받는사람",
                createAndSaveMember("받는사람", "recipient@email.com"),
                organization
        );
        var event = createAndSaveEvent(sender, organization);
        var now = LocalDateTime.now();

        for (int i = 0; i < 10; i++) {
            pokeHistoryRepository.save(PokeHistory.create(sender, recipient, event, now.minusMinutes(i)));
        }

        // when // then
        assertThatThrownBy(() -> sut.doPoke(sender, recipient, event, now))
                .isInstanceOf(BusinessRuleViolatedException.class)
                .hasMessage("포키는 30분마다 한 대상에게 최대 10번만 보낼 수 있습니다.");
    }

    @Test
    void 단일_대상에게_포키를_보낼_수_있다() {
        // given
        var organization = createAndSaveOrganization();
        var sender = createAndSaveOrganizationMember(
                "보내는사람",
                createAndSaveMember("보내는사람", "sender@email.com"),
                organization
        );
        var recipient = createAndSaveOrganizationMember(
                "받는사람",
                createAndSaveMember("받는사람", "recipient@email.com"),
                organization
        );
        var event = createAndSaveEvent(sender, organization);
        var now = LocalDateTime.now();

        // when
        sut.doPoke(sender, recipient, event, now);

        // then
        var histories = pokeHistoryRepository.findAll();
        assertThat(histories).hasSize(1);
        var history = histories.get(0);
        assertThat(history.getSender()).isEqualTo(sender);
        assertThat(history.getRecipient()).isEqualTo(recipient);
        assertThat(history.getEvent()).isEqualTo(event);

        verify(pushNotifier, times(1)).sendPush(any(), any());
    }

    private Member createAndSaveMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "testPicture"));
    }

    private Organization createAndSaveOrganization() {
        return organizationRepository.save(Organization.create("조직", "설명", "img.png"));
    }

    private OrganizationMember createAndSaveOrganizationMember(String nickname, Member member, Organization org) {
        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, org));
    }

    private Event createAndSaveEvent(OrganizationMember organizer, Organization organization, Question... questions) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(3), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(6)
                ),
                100,
                questions
        );

        return eventRepository.save(event);
    }
}
