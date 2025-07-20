package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Guest;
import com.ahmadda.domain.GuestRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Period;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventGuestServiceTest {

    @Autowired
    private EventGuestService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    void 이벤트에_참여한_게스트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization();
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), organization);
        var event = createAndSaveEvent(organizer, organization);

        var guest1 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember("게스트1", createAndSaveMember("게스트1", "g1@email.com"), organization)
        );
        var guest2 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember("게스트2", createAndSaveMember("게스트2", "g2@email.com"), organization)
        );

        // when
        var result = sut.getGuests(event.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(guest1, guest2);
        });
    }

    @Test
    void 이벤트에_참여하지_않은_조직원들을_조회한다() {
        // given
        var org = createAndSaveOrganization();
        var organizer = createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), org);
        var event = createAndSaveEvent(organizer, org);

        var guest = createAndSaveOrganizationMember("게스트", createAndSaveMember("게스트", "g@email.com"), org);
        var nonGuest1 = createAndSaveOrganizationMember("비게스트1", createAndSaveMember("비게스트1", "ng1@email.com"), org);
        var nonGuest2 = createAndSaveOrganizationMember("비게스트2", createAndSaveMember("비게스트2", "ng2@email.com"), org);
        createAndSaveGuest(event, guest);

        // when
        var result = sut.getNonGuestOrganizationMembers(event.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(nonGuest1, nonGuest2);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_게스트_조회시_예외가_발생한다() {
        assertThatThrownBy(() -> sut.getGuests(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 존재하지_않는_이벤트로_비게스트_조회시_예외가_발생한다() {
        assertThatThrownBy(() -> sut.getNonGuestOrganizationMembers(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    private Member createAndSaveMember(String name, String email) {
        return memberRepository.save(Member.create(name, email));
    }

    private Organization createAndSaveOrganization() {
        return organizationRepository.save(Organization.create("조직", "설명", "img.png"));
    }

    private OrganizationMember createAndSaveOrganizationMember(String nickname, Member member, Organization org) {
        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, org));
    }

    private Event createAndSaveEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        new Period(now.minusDays(3), now.minusDays(1)),
                        new Period(now.plusDays(1), now.plusDays(2)),
                        now.minusDays(6)
                ),
                100
        );

        return eventRepository.save(event);
    }

    private Guest createAndSaveGuest(Event event, OrganizationMember member) {
        return guestRepository.save(Guest.create(event, member, event.getRegistrationStart()));
    }
}
