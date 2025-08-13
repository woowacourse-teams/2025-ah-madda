package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventNotificationOptOutRepository;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class EventNotificationOptOutServiceTest {

    @Autowired
    private EventNotificationOptOutService sut;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private EventNotificationOptOutRepository optOutRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void 이벤트에_대한_알림_수신_거부를_설정할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember("user", "user@mail.com");
        var organizationMember = createOrganizationMember("닉네임", member, organization);
        var event = createEvent(organizationMember, organization);
        var loginMember = new LoginMember(member.getId());

        // when
        var saved = sut.optOut(event.getId(), loginMember);

        // then
        assertThat(optOutRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(savedOptOut -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedOptOut.getEvent())
                                .isEqualTo(event);
                        softly.assertThat(savedOptOut.getOrganizationMember())
                                .isEqualTo(organizationMember);
                    });
                });
    }

    @Test
    void 이미_수신거부가_설정된_경우_예외가_발생한다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());
        sut.optOut(event.getId(), loginMember);

        // when // then
        assertThatThrownBy(() -> sut.optOut(event.getId(), loginMember))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("이미 해당 이벤트에 대한 알림 수신 거부가 설정되어 있습니다.");
    }

    @Test
    void 알림_수신거부를_취소할_수_있다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());
        sut.optOut(event.getId(), loginMember);

        // when
        sut.cancelOptOut(event.getId(), loginMember);

        // then
        assertThat(optOutRepository.existsByEventAndOrganizationMember(event, orgMember)).isFalse();
    }

    @Test
    void 수신거부_정보가_없으면_취소시_예외가_발생한다() {
        // given
        var org = createOrganization();
        var member = createMember("user", "user@mail.com");
        var orgMember = createOrganizationMember("닉네임", member, org);
        var event = createEvent(orgMember, org);

        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.cancelOptOut(event.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("수신 거부 설정이 존재하지 않습니다.");
    }

    private Organization createOrganization() {
        return organizationRepository.save(Organization.create("조직", "설명", "image.png"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "picture"));
    }

    private OrganizationMember createOrganizationMember(String nickname, Member member, Organization org) {
        return organizationMemberRepository.save(OrganizationMember.create(nickname, member, org));
    }

    private Event createEvent(OrganizationMember organizer, Organization organization) {
        var now = LocalDateTime.now();
        return eventRepository.save(Event.create(
                "이벤트 제목",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(3),
                        now.minusDays(1),
                        now.plusDays(1),
                        now.plusDays(2),
                        now.minusDays(5)
                ),
                100
        ));
    }
}
