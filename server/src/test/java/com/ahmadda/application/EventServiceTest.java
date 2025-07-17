package com.ahmadda.application;

import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EventService sut;

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
                                     String description,
                                     String place,
                                     OrganizationMember organizer,
                                     Organization organization,
                                     LocalDateTime eventStart,
                                     LocalDateTime eventEnd,
                                     int maxCapacity) {
        var event = Event.create(
                title,
                description,
                place,
                organizer,
                organization,
                LocalDateTime.now().minusDays(10), // registrationStart
                LocalDateTime.now().minusDays(1),  // registrationEnd
                eventStart,
                eventEnd,
                maxCapacity
        );

        return eventRepository.save(event);
    }

    @Test
    void 조직의_미래_이벤트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization("테스트 조직", "조직 설명", "org.png");
        var member = createAndSaveMember("주최자", "organizer@test.com");
        var organizer = createAndSaveOrganizationMember("주최자닉네임", member, organization);

        var futureEvent1 = createAndSaveEvent(
                "미래 이벤트 1",
                "첫 번째 미래 이벤트",
                "장소1",
                organizer,
                organization,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                50
        );

        var futureEvent2 = createAndSaveEvent(
                "미래 이벤트 2",
                "두 번째 미래 이벤트",
                "장소2",
                organizer,
                organization,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4),
                30
        );

        // 과거 이벤트 (결과에 포함되지 않아야 함)
        createAndSaveEvent(
                "과거 이벤트",
                "과거 이벤트 설명",
                "과거장소",
                organizer,
                organization,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                20
        );

        // when
        var result = sut.getOrganizationAvailableEvents(organization.getId());

        // then
        assertThat(result).hasSize(2);

        SoftAssertions.assertSoftly(softly -> {
            var firstEvent = result.get(0);
            softly.assertThat(firstEvent.getTitle()).isEqualTo("미래 이벤트 1");
            softly.assertThat(firstEvent.getDescription()).isEqualTo("첫 번째 미래 이벤트");
            softly.assertThat(firstEvent.getPlace()).isEqualTo("장소1");
            softly.assertThat(firstEvent.getMaxCapacity()).isEqualTo(50);
            softly.assertThat(firstEvent.getOrganizer().getNickname()).isEqualTo("주최자닉네임");

            var secondEvent = result.get(1);
            softly.assertThat(secondEvent.getTitle()).isEqualTo("미래 이벤트 2");
            softly.assertThat(secondEvent.getDescription()).isEqualTo("두 번째 미래 이벤트");
            softly.assertThat(secondEvent.getPlace()).isEqualTo("장소2");
            softly.assertThat(secondEvent.getMaxCapacity()).isEqualTo(30);
            softly.assertThat(secondEvent.getOrganizer().getNickname()).isEqualTo("주최자닉네임");
        });
    }

    @Test
    void 조직에_미래_이벤트가_없으면_빈_리스트를_반환한다() {
        // given
        var organization = createAndSaveOrganization("빈 조직", "이벤트 없는 조직", "empty.png");
        var member = createAndSaveMember("주최자", "organizer@test.com");
        var organizer = createAndSaveOrganizationMember("주최자닉네임", member, organization);

        // 과거 이벤트만 존재
        createAndSaveEvent(
                "과거 이벤트",
                "과거 이벤트 설명",
                "과거장소",
                organizer,
                organization,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                20
        );

        // when
        var result = sut.getOrganizationAvailableEvents(organization.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_조직ID로_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.getOrganizationAvailableEvents(999L))
                .isInstanceOf(BusinessFlowViolatedException.class);
    }

    @Test
    void 현재_시간_경계값_테스트() {
        // given
        var organization = createAndSaveOrganization("경계값 조직", "시간 경계값 테스트", "boundary.png");
        var member = createAndSaveMember("주최자", "organizer@test.com");
        var organizer = createAndSaveOrganizationMember("주최자닉네임", member, organization);

        var now = LocalDateTime.now();

        // 현재 시간 1초 후 시작하는 이벤트 (포함되어야 함)
        createAndSaveEvent(
                "경계값 이벤트",
                "경계값 테스트",
                "경계장소",
                organizer,
                organization,
                now.plusSeconds(1),
                now.plusHours(2),
                10
        );

        // 현재 시간 1초 전에 시작한 이벤트 (포함되지 않아야 함)
        createAndSaveEvent(
                "과거 경계값 이벤트",
                "과거 경계값 테스트",
                "과거경계장소",
                organizer,
                organization,
                now.minusSeconds(1),
                now.plusHours(1),
                10
        );

        // when
        var result = sut.getOrganizationAvailableEvents(organization.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("경계값 이벤트");
    }

    @Test
    void 내가_속한_조직에서_주최한_이벤트만_조회한다() {
        // given
        var organization1 = createAndSaveOrganization("우리 조직", "내 이벤트가 있는 조직", "org1.png");
        var organization2 = createAndSaveOrganization("다른 조직", "남의 이벤트가 있는 조직", "org2.png");

        var owner = createAndSaveMember("주최자 본인", "owner@test.com");
        var otherMember = createAndSaveMember("다른 멤버", "other@test.com");

        var organizer = createAndSaveOrganizationMember("주최자닉네임", owner, organization1);
        var otherOrganizer = createAndSaveOrganizationMember("다른주최자닉네임", otherMember, organization1);

        createAndSaveEvent("내 이벤트 1", "설명1", "장소1", organizer, organization1,
                           LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 50
        );
        createAndSaveEvent("내 이벤트 2", "설명2", "장소2", organizer, organization1,
                           LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), 50
        );

        createAndSaveEvent("남의 이벤트", "설명3", "장소3", otherOrganizer, organization1,
                           LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6), 30
        );

        var organizerInOtherOrg = createAndSaveOrganizationMember("다른조직의내닉네임", owner, organization2);
        createAndSaveEvent("내가 다른 조직에 만든 이벤트", "설명4", "장소4", organizerInOtherOrg, organization2,
                           LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(8), 20
        );

        // when
        var result = sut.getOwnersEvent(owner.getId(), organization1.getId());

        // then
        assertThat(result).hasSize(2);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result).extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("내 이벤트 1", "내 이벤트 2");
            softly.assertThat(result).allMatch(event -> event.getOrganization().getId().equals(organization1.getId()));
            softly.assertThat(result).allMatch(event -> event.getOrganizer().getMember().getId().equals(owner.getId()));
        });
    }

    @Test
    void 내가_주최한_이벤트가_없으면_빈_리스트를_반환한다() {
        // given
        var organization = createAndSaveOrganization("우리 조직", "내 이벤트가 없는 조직", "org.png");
        var owner = createAndSaveMember("주최자 본인", "owner@test.com");
        var otherMember = createAndSaveMember("다른 멤버", "other@test.com");

        createAndSaveOrganizationMember("주최자닉네임", owner, organization);
        var otherOrganizer = createAndSaveOrganizationMember("다른주최자닉네임", otherMember, organization);

        createAndSaveEvent("남의 이벤트", "설명", "장소", otherOrganizer, organization,
                           LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 50
        );

        // when
        var result = sut.getOwnersEvent(owner.getId(), organization.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 존재하지_않는_멤버나_조직_ID로_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization("우리 조직", "조직", "org.png");
        var owner = createAndSaveMember("주최자 본인", "owner@test.com");
        createAndSaveOrganizationMember("주최자닉네임", owner, organization);

        var nonExistentMemberId = 999L;
        var nonExistentOrganizationId = 998L;

        // when // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> sut.getOwnersEvent(nonExistentMemberId, organization.getId()))
                    .isInstanceOf(BusinessFlowViolatedException.class);
            softly.assertThatThrownBy(() -> sut.getOwnersEvent(owner.getId(), nonExistentOrganizationId))
                    .isInstanceOf(BusinessFlowViolatedException.class);
        });
    }
}
