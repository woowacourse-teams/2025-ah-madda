package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventPokeReminder;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.PushNotifier;
import com.ahmadda.presentation.dto.NotifyPokeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional
class EventPokeServiceTest {

    @Autowired
    private EventPokeService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private EventPokeReminder eventPokeReminder;

    @MockitoBean
    private PushNotifier pushNotifier;

    @Test
    void 포키를_성공적으로_전송한다() {
        // given
        var organization = createOrganization("테스트 조직", "조직 설명", "test-image.png");
        var member = createMember("테스트 회원", "test@example.com", "test-profile.png");
        var organizer = createOrganizationMember("주최자", member, organization);
        var participantMember = createMember("참여자", "participant@example.com", "participant-profile.png");
        var participant = createOrganizationMember("참여자", participantMember, organization);
        var event = createEvent("테스트 이벤트", "이벤트 설명", "테스트 장소", organizer, organization);

        var eventId = event.getId();
        var request = new NotifyPokeRequest(participant.getId());
        var loginMember = new LoginMember(organizer.getId());

        // when
        sut.poke(eventId, request, loginMember);

        // then
        verify(pushNotifier, times(1)).sendPush(eq(participant), any());
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
        var request = new NotifyPokeRequest(participant.getId());
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
        var request = new NotifyPokeRequest(999L); // 존재하지 않는 수신자 ID
        var loginMember = new LoginMember(organizer.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("조직원을 찾는데 실패하였습니다.");
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
        var request = new NotifyPokeRequest(participant.getId());
        var loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("조직원을 찾는데 실패하였습니다.");
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
        var request = new NotifyPokeRequest(participant.getId());
        var loginMember = new LoginMember(otherMember.getId());

        // when // then
        assertThatThrownBy(() -> sut.poke(eventId, request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("조직원을 찾는데 실패하였습니다.");
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
        var organizationMember = OrganizationMember.create(nickname, member, organization);
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
