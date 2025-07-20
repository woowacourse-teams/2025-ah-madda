package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService sut;

    @Test
    void 이벤트를_생성할_수_있다() {
        //given
        var member = appendMember();
        var organization = appendOrganization();
        var organizationMember = appendOrganizationMember(organization, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                new Period(now.plusDays(3), now.plusDays(4)),
                new Period(now.plusDays(5), now.plusDays(6)),
                100,
                organizationMember.getId(),
                organization.getId()
        );

        //when
        var event = sut.createEvent(eventCreateRequest);

        //then
        assertThat(eventRepository.findById(event.getId()))
                .isPresent()
                .hasValueSatisfying(savedEvent -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedEvent.getTitle()).isEqualTo("UI/UX 이벤트");
                        softly.assertThat(savedEvent.getDescription()).isEqualTo("UI/UX 이벤트 입니다");
                        softly.assertThat(savedEvent.getPlace()).isEqualTo("선릉");
                        softly.assertThat(savedEvent.getOrganization()).isEqualTo(organization);
                        softly.assertThat(savedEvent.getOrganizer()).isEqualTo(organizationMember);
                        softly.assertThat(savedEvent.getEventOperationPeriod()).isEqualTo(EventOperationPeriod.create(
                                new Period(now.plusDays(3), now.plusDays(4)),
                                new Period(now.plusDays(5), now.plusDays(6)),
                                now
                        ));
                    });
                });
    }

    @Test
    void 이벤트_생성시_조직_id에_해당하는_조직이_없다면_예외가_발생한다() {
        //given
        var organizationMember = appendOrganizationMember(appendOrganization(), appendMember());

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                new Period(now.plusDays(3), now.plusDays(4)),
                new Period(now.plusDays(5), now.plusDays(6)),
                100,
                organizationMember.getId(),
                999L
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(eventCreateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("999에 해당하는 조직을 찾을 수 없습니다.");
    }

    @Test
    void 이벤트_생성시_조직원_id에_해당하는_조직원이_없다면_예외가_발생한다() {
        //given
        var organization = appendOrganization();

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                new Period(now.plusDays(3), now.plusDays(4)),
                new Period(now.plusDays(5), now.plusDays(6)),
                100,
                999L,
                organization.getId()
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(eventCreateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("999에 해당하는 조직원을 찾을 수 없습니다.");
    }

    private Organization appendOrganization() {
        var organization = Organization.create("우테코", "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private Member appendMember() {
        return memberRepository.save(Member.create("name", "ahmadda@ahmadda.com"));
    }

    private OrganizationMember appendOrganizationMember(Organization organization, Member member) {
        var organizationMember = OrganizationMember.create("surf", member, organization);

        return organizationMemberRepository.save(organizationMember);
    }
}
