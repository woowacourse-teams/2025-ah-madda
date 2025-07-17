package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class GuestServiceTest {

    @Autowired
    private GuestService sut;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void 특정_조직에서_참여한_이벤트_목록을_조회한다() {
        // given
        var organization1 = createAndSaveOrganization("우리 조직", "설명1", "org1.png");
        var organization2 = createAndSaveOrganization("다른 조직", "설명2", "org2.png");

        var participant = createAndSaveMember("참여자 본인", "me@test.com");
        var otherParticipant = createAndSaveMember("다른 참여자", "other@test.com");

        var meInOrg1 = createAndSaveOrganizationMember("내닉네임_org1", participant, organization1);
        var meInOrg2 = createAndSaveOrganizationMember("내닉네임_org2", participant, organization2);
        var otherInOrg1 = createAndSaveOrganizationMember("남의닉네임_org1", otherParticipant, organization1);

        var event1InOrg1 = createAndSaveEvent("우리 조직 이벤트 1", meInOrg1, organization1);
        var event2InOrg1 = createAndSaveEvent("우리 조직 이벤트 2", otherInOrg1, organization1);
        var eventInOrg2 = createAndSaveEvent("다른 조직 이벤트", meInOrg2, organization2);

        createAndSaveGuest(event1InOrg1, meInOrg1);
        createAndSaveGuest(event2InOrg1, meInOrg1);
        createAndSaveGuest(event1InOrg1, otherInOrg1);
        createAndSaveGuest(eventInOrg2, meInOrg2);

        // when
        var result = sut.getJoinedEvents(participant.getId(), organization1.getId());

        // then
        assertThat(result).hasSize(2);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("우리 조직 이벤트 1", "우리 조직 이벤트 2");
            softly.assertThat(result).allMatch(event -> event.getOrganization().getId().equals(organization1.getId()));
        });
    }

    @Test
    void 특정_조직에서_참여한_이벤트가_없으면_빈_리스트를_반환한다() {
        // given
        var organization1 = createAndSaveOrganization("우리 조직", "설명1", "org1.png");
        var organization2 = createAndSaveOrganization("다른 조직", "설명2", "org2.png");
        var participant = createAndSaveMember("참여자", "me@test.com");

        var meInOrg2 = createAndSaveOrganizationMember("내닉네임_org2", participant, organization2);
        var eventInOrg2 = createAndSaveEvent("다른 조직 이벤트", meInOrg2, organization2);

        createAndSaveGuest(eventInOrg2, meInOrg2);

        // when
        var result = sut.getJoinedEvents(participant.getId(), organization1.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_멤버나_조직으로_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization("우리 조직", "조직", "org.png");
        var participant = createAndSaveMember("참여자", "me@test.com");

        var nonExistentMemberId = 999L;
        var nonExistentOrganizationId = 998L;

        // when // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> sut.getJoinedEvents(nonExistentMemberId, organization.getId()))
                    .isInstanceOf(BusinessFlowViolatedException.class);
            softly.assertThatThrownBy(() -> sut.getJoinedEvents(participant.getId(), nonExistentOrganizationId))
                    .isInstanceOf(BusinessFlowViolatedException.class);
        });
    }

    private Organization createAndSaveOrganization(String name, String description, String imageUrl) {
        var organization = Organization.create(name, description, imageUrl);
        return organizationRepository.save(organization);
    }

    private Member createAndSaveMember(String name, String email) {
        var member = Member.create(name, email);
        return memberRepository.save(member);
    }

    private OrganizationMember createAndSaveOrganizationMember(String nickname,
                                                               Member member,
                                                               Organization organization) {
        var organizationMember = OrganizationMember.create(nickname, member, organization);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createAndSaveEvent(String title,
                                     OrganizationMember organizer,
                                     Organization organization) {
        var event = Event.create(
                title,
                "설명",
                "장소",
                organizer,
                organization,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(20),
                LocalDateTime.now().plusDays(21),
                50
        );
        return eventRepository.save(event);
    }

    private Guest createAndSaveGuest(Event event, OrganizationMember participant) {
        var guest = Guest.create(event, participant);
        return guestRepository.save(guest);
    }
}
