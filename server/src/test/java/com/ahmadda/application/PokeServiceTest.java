package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventNotificationOptOut;
import com.ahmadda.domain.EventNotificationOptOutRepository;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Poke;
import com.ahmadda.domain.PokeHistoryRepository;
import com.ahmadda.domain.Role;
import com.ahmadda.presentation.dto.PokeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
class PokeServiceTest {

    @Autowired
    private PokeService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @MockitoSpyBean
    private Poke poke;

    @Autowired
    private EventNotificationOptOutRepository eventNotificationOptOutRepository;
    @Autowired
    private PokeHistoryRepository pokeHistoryRepository;

    @Test
    void 포키를_할_수_있다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        // when
        sut.poke(event.getId(), new PokeRequest(participant.getId()), new LoginMember(member.getId()));

        // then
        verify(poke).doPoke(eq(organizer), eq(participant), eq(event), any());
    }

    @Test
    void 포키를_성공적으로_전송하면_이력을_저장한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        var eventId = event.getId();
        var request = new PokeRequest(participant.getId());
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.poke(eventId, request, loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(pokeHistoryRepository.count())
                    .isEqualTo(1L);
            var pokeHistory = pokeHistoryRepository.getReferenceById(result.getId());
            softly.assertThat(pokeHistory
                            .getRecipient())
                    .isEqualTo(participant);
            softly.assertThat(pokeHistory
                            .getSender())
                    .isEqualTo(organizer);
            softly.assertThat(pokeHistory
                            .getEvent())
                    .isEqualTo(event);
        });
    }

    @Test
    void 존재하지_않는_이벤트에서_포키_전송시_예외가_발생한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);

        var nonExistentEventId = 999L;
        var request = new PokeRequest(participant.getId());
        var loginMember = new LoginMember(organizer.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(nonExistentEventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 존재하지_않는_조직원에게로_포키_전송시_예외가_발생한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        var eventId = event.getId();
        var request = new PokeRequest(999L);
        var loginMember = new LoginMember(organizer.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 존재하지_않는_조직원이_포키_전송시_예외가_발생한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        var eventId = event.getId();
        var request = new PokeRequest(participant.getId());
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 조직에_속하지_않는_회원이_포키_전송시_예외가_발생한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        var otherOrganization = createOrganization("다른 조직", "다른 조직 설명", "other-image.png");
        var otherMember = createMember("다른 조직 회원", "other@example.com", "other-profile.png");
        var otherOrganizationMember = createOrganizationMember("다른 조직원", otherMember, otherOrganization);

        var eventId = event.getId();
        var request = new PokeRequest(participant.getId());
        var loginMember = new LoginMember(otherMember.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    @Test
    void 알림_수신을_거부한_조직원에게_포키_전송시_예외가_발생한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        eventNotificationOptOutRepository.save(
                EventNotificationOptOut.create(participant, event)
        );

        var eventId = event.getId();
        var request = new PokeRequest(participant.getId());
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("알림을 받지 않는 조직원입니다.");
    }

    private Organization createOrganization(String name, String description, String imageUrl) {
        var organization = Organization.create(name, description, imageUrl);

        return organizationRepository.save(organization);
    }

    private Member createMember(String name, String email, String profileImageUrl) {
        var member = Member.create(name, email, profileImageUrl);

        return memberRepository.save(member);
    }

    private OrganizationMember createOrganizationMember(String nickname, Member member, Organization organization) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, Role.USER);

        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(
            String title,
            String description,
            String place,
            OrganizationMember organizer,
            Organization organization
    ) {
        var now = LocalDateTime.now();
        var period = EventOperationPeriod.create(
                now.plusDays(1), now.plusDays(2), now.plusDays(3), now.plusDays(4), now
        );
        var event = Event.create(title, description, place, organizer, organization, period, 100, new ArrayList<>());

        return eventRepository.save(event);
    }
}
