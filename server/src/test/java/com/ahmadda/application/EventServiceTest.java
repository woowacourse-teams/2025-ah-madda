package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
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
import com.ahmadda.domain.Question;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                now.plusDays(3), now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        //when
        var event = sut.createEvent(organization.getId(), organizationMember.getId(), eventCreateRequest);

        //then
        assertThat(eventRepository.findById(event.getId()))
                .isPresent()
                .hasValueSatisfying(savedEvent -> {
                    assertSoftly(softly -> {
                        softly.assertThat(savedEvent.getTitle())
                                .isEqualTo("UI/UX 이벤트");
                        softly.assertThat(savedEvent.getDescription())
                                .isEqualTo("UI/UX 이벤트 입니다");
                        softly.assertThat(savedEvent.getPlace())
                                .isEqualTo("선릉");
                        softly.assertThat(savedEvent.getOrganization())
                                .isEqualTo(organization);
                        softly.assertThat(savedEvent.getOrganizer())
                                .isEqualTo(organizationMember);
                        softly.assertThat(savedEvent.getEventOperationPeriod())
                                .isEqualTo(EventOperationPeriod.create(
                                        Period.create(now.plusDays(3), now.plusDays(4)),
                                        Period.create(now.plusDays(5), now.plusDays(6)),
                                        now
                                ));
                        List<Question> questions = savedEvent.getQuestions();
                        softly.assertThat(questions)
                                .hasSize(2)
                                .extracting("questionText", "isRequired", "orderIndex")
                                .containsExactly(Tuple.tuple("1번 질문", true, 0), Tuple.tuple("2번 질문", false, 1));
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
                now.plusDays(3), now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(999L, organizationMember.getId(), eventCreateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 조직 정보입니다.");
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
                now.plusDays(3), now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization.getId(), 999L, eventCreateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_생성시_요청한_조직에_소속되지_않았다면_예외가_발생한다() {
        //given
        var organization1 = appendOrganization();
        var organization2 = appendOrganization();
        Member member = appendMember();
        appendOrganizationMember(organization2, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(3), now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization1.getId(), member.getId(), eventCreateRequest))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("조직에 소속되지 않은 멤버입니다.");
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
