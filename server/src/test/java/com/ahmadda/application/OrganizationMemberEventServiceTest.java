package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class OrganizationMemberEventServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private OrganizationMemberEventService sut;

    @MockitoBean
    JwtAccessTokenProperties accessTokenProperties;

    @MockitoBean
    JwtRefreshTokenProperties refreshTokenProperties;

    @Test
    void 구성원이_주최한_이벤트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization("테스트 이벤트 스페이스", "이벤트 스페이스 설명", "org.png");
        var member = createAndSaveMember("주최자", "organizer@test.com");
        var organizer = createAndSaveOrganizationMember("주최자닉네임", member, organization);

        var event1 = createAndSaveEvent(
                "주최 이벤트 1",
                "첫 번째 주최 이벤트",
                "장소1",
                organizer,
                organization,
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2),
                50
        );

        var event2 = createAndSaveEvent(
                "주최 이벤트 2",
                "두 번째 주최 이벤트",
                "장소2",
                organizer,
                organization,
                LocalDateTime.now()
                        .plusDays(3),
                LocalDateTime.now()
                        .plusDays(4),
                30
        );

        var otherMember = createAndSaveMember("다른 주최자", "other@test.com");
        var otherOrganizer = createAndSaveOrganizationMember("다른주최자닉네임", otherMember, organization);
        createAndSaveEvent(
                "다른 이벤트",
                "다른 주최자의 이벤트",
                "다른장소",
                otherOrganizer,
                organization,
                LocalDateTime.now()
                        .plusDays(5),
                LocalDateTime.now()
                        .plusDays(6),
                20
        );

        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getOwnerEvents(organization.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            var firstEvent = result.get(0);
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(firstEvent.getTitle())
                    .isEqualTo("주최 이벤트 1");
            softly.assertThat(firstEvent.getDescription())
                    .isEqualTo("첫 번째 주최 이벤트");
            softly.assertThat(firstEvent.getPlace())
                    .isEqualTo("장소1");
            softly.assertThat(firstEvent.getMaxCapacity())
                    .isEqualTo(50);

            var secondEvent = result.get(1);
            softly.assertThat(secondEvent.getTitle())
                    .isEqualTo("주최 이벤트 2");
            softly.assertThat(secondEvent.getDescription())
                    .isEqualTo("두 번째 주최 이벤트");
            softly.assertThat(secondEvent.getPlace())
                    .isEqualTo("장소2");
            softly.assertThat(secondEvent.getMaxCapacity())
                    .isEqualTo(30);
        });
    }

    @Test
    void 구성원이_참여한_이벤트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization("테스트 이벤트 스페이스", "이벤트 스페이스 설명", "org.png");
        var organizerMember = createAndSaveMember("주최자", "organizer@test.com");
        var participantMember = createAndSaveMember("참여자", "participant@test.com");

        var organizer = createAndSaveOrganizationMember("주최자닉네임", organizerMember, organization);
        var participant = createAndSaveOrganizationMember("참여자닉네임", participantMember, organization);

        var event1 = createAndSaveEvent(
                "참여 이벤트 1",
                "첫 번째 참여 이벤트",
                "장소1",
                organizer,
                organization,
                LocalDateTime.now()
                        .plusDays(1),
                LocalDateTime.now()
                        .plusDays(2),
                50
        );

        var event2 = createAndSaveEvent(
                "참여 이벤트 2",
                "두 번째 참여 이벤트",
                "장소2",
                organizer,
                organization,
                LocalDateTime.now()
                        .plusDays(3),
                LocalDateTime.now()
                        .plusDays(4),
                30
        );

        // 참여하지 않는 이벤트 (결과에 포함되지 않아야 함)
        createAndSaveEvent(
                "미참여 이벤트",
                "참여하지 않는 이벤트",
                "미참여장소",
                organizer,
                organization,
                LocalDateTime.now()
                        .plusDays(5),
                LocalDateTime.now()
                        .plusDays(6),
                20
        );

        // 참여 등록
        createAndSaveGuest(event1, participant);
        createAndSaveGuest(event2, participant);

        // when
        var result = sut.getParticipantEvents(
                organization.getId(),
                new LoginMember(participantMember.getId())
        );

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .extracting(Event::getTitle)
                    .containsExactlyInAnyOrder("참여 이벤트 1", "참여 이벤트 2");
            softly.assertThat(result)
                    .extracting(Event::getPlace)
                    .containsExactlyInAnyOrder("장소1", "장소2");
        });
    }

    @Test
    void 존재하지_않는_회원으로_주최_이벤트_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization("테스트 이벤트 스페이스", "이벤트 스페이스 설명", "org.png");
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.getOwnerEvents(organization.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원 정보입니다.");
    }

    @Test
    void 존재하지_않는_구성원으로_참여_이벤트_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization("테스트 이벤트 스페이스", "이벤트 스페이스 설명", "org.png");
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.getParticipantEvents(organization.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원 정보입니다.");
    }

    private Organization createAndSaveOrganization(String name, String description, String imageUrl) {
        var organization = Organization.create(name, description, imageUrl);
        return organizationRepository.save(organization);
    }

    private Member createAndSaveMember(String name, String email) {
        var member = Member.create(name, email, "testPicture");
        return memberRepository.save(member);
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            Member member,
            Organization organization
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, OrganizationMemberRole.USER);
        return organizationMemberRepository.save(organizationMember);
    }

    private Event createAndSaveEvent(
            String title,
            String description,
            String place,
            OrganizationMember organizer,
            Organization organization,
            LocalDateTime eventStart,
            LocalDateTime eventEnd,
            int maxCapacity
    ) {
        var event = Event.create(
                title,
                description,
                place,
                organizer,
                organization,
                EventOperationPeriod.create(
                        LocalDateTime.now()
                                .minusDays(10),
                        LocalDateTime.now()
                                .minusDays(1),
                        eventStart,
                        eventEnd,
                        LocalDateTime.now()
                                .minusDays(20)
                ),
                maxCapacity
        );

        return eventRepository.save(event);
    }

    private Guest createAndSaveGuest(Event event, OrganizationMember participant) {
        var guest = Guest.create(event, participant, event.getRegistrationStart());

        return guestRepository.save(guest);
    }
}
