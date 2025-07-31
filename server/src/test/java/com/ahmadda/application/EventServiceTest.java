package com.ahmadda.application;

import com.ahmadda.application.dto.EventCreateRequest;
import com.ahmadda.application.dto.QuestionCreateRequest;
import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Email;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventNotification;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

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

    @MockitoBean
    private EventNotification eventNotification;

    @Test
    void 이벤트를_생성할_수_있다() {
        //given
        var member = createMember();
        var organization = createOrganization();
        var organizationMember = createOrganizationMember(organization, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        //when
        var event = sut.createEvent(organization.getId(), organizationMember.getId(), eventCreateRequest, now);

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
                                        Period.create(now, now.plusDays(4)),
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
        var organizationMember = createOrganizationMember(createOrganization(), createMember());

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(new QuestionCreateRequest("1번 질문", true), new QuestionCreateRequest("2번 질문", false))
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(999L, organizationMember.getId(), eventCreateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 조직 정보입니다.");
    }

    @Test
    void 이벤트_생성시_조직원_id에_해당하는_조직원이_없다면_예외가_발생한다() {
        //given
        var organization = createOrganization();

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization.getId(), 999L, eventCreateRequest, now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_생성시_요청한_조직에_소속되지_않았다면_예외가_발생한다() {
        //given
        var organization1 = createOrganization();
        var organization2 = createOrganization();
        Member member = createMember();
        createOrganizationMember(organization2, member);

        var now = LocalDateTime.now();
        var eventCreateRequest = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5), now.plusDays(6),
                "이밴트 근로",
                100,
                new ArrayList<>()
        );

        //when //then
        assertThatThrownBy(() -> sut.createEvent(organization1.getId(), member.getId(), eventCreateRequest, now))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("조직에 소속되지 않은 멤버입니다.");
    }

    @Test
    void 이벤트를_조회할_수_있다() {
        //given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organizationMember, organization);

        //when
        var findEvent = sut.getEvent(event.getId());

        //then
        assertThat(findEvent).isEqualTo(event);
    }

    @Test
    void 이벤트_ID를_이용해_이벤트를_조회할때_해당_이벤트가_없다면_예외가_발생한다() {
        //when //then
        assertThatThrownBy(() -> sut.getEvent(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 이벤트 정보입니다.");
    }

    @Test
    void 이벤트_마감_시_조직_id에_해당하는_조직이_없다면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organizationMember, organization);
        var now = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> sut.closeEventRegistration(999L, event.getId(), organizationMember.getId(), now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않은 조직 정보입니다.");
    }

    @Test
    void 이벤트_마감_시_조직원_id에_해당하는_조직원이_없다면_예외가_발생한다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var organizationMember = createOrganizationMember(organization, member);
        var event = createEvent(organizationMember, organization);
        var now = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> sut.closeEventRegistration(
                organization.getId(),
                event.getId(),
                999L,
                now
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 이벤트_마감_시_요청한_조직에_소속되지_않았다면_예외가_발생한다() {
        // given
        var organization1 = createOrganization();
        var organization2 = createOrganization();
        var member = createMember();
        var notBelongingOrgMember = createOrganizationMember(organization2, member);
        var event = createEvent(notBelongingOrgMember, organization2);
        var now = LocalDateTime.now();

        // when // then
        assertThatThrownBy(() -> sut.closeEventRegistration(
                organization1.getId(),
                event.getId(),
                notBelongingOrgMember.getId(),
                now
        ))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("조직에 소속되지 않은 멤버입니다.");
    }

    @Test
    void 주최자는_이벤트를_수동으로_마감할_수_있다() {
        // given
        var organization = createOrganization();
        var member = createMember();
        var orgMember = createOrganizationMember(organization, member);
        var event = createEvent(orgMember, organization);
        var now = LocalDateTime.now();

        // when // then
        assertDoesNotThrow(() -> sut.closeEventRegistration(
                organization.getId(),
                event.getId(),
                orgMember.getId(),
                now.plusDays(2)
                        .plusHours(6)
        ));
    }

    @Test
    void 이벤트_생성_시_조직원에게_알림을_보낸다() {
        // given
        var organization = createOrganization();

        var organizerMember = createMember("organizer", "organizer@mail.com");
        var om1Member = createMember("m1", "m1@mail.com");
        var om2Member = createMember("m2", "m2@mail.com");

        var organizer = createOrganizationMember(organization, organizerMember);
        createOrganizationMember(organization, om1Member);
        createOrganizationMember(organization, om2Member);

        var now = LocalDateTime.now();
        var request = new EventCreateRequest(
                "UI/UX 이벤트",
                "UI/UX 이벤트 입니다",
                "선릉",
                now.plusDays(4),
                now.plusDays(5),
                now.plusDays(6),
                "이벤트 근로",
                100,
                List.of(
                        new QuestionCreateRequest("1번 질문", true),
                        new QuestionCreateRequest("2번 질문", false)
                )
        );

        // when
        var savedEvent = sut.createEvent(organization.getId(), organizer.getId(), request, now);

        // then
        var email = Email.of(savedEvent, "새로운 이벤트가 등록되었습니다.");
        verify(eventNotification).sendEmails(
                argThat(recipients -> {
                    var emails = recipients.stream()
                            .map(om -> om.getMember()
                                    .getEmail())
                            .collect(toSet());

                    var expected = Set.of(
                            om1Member.getEmail(),
                            om2Member.getEmail()
                    );

                    return emails.equals(expected);
                }),
                eq(email)
        );
    }

    private Organization createOrganization() {
        var organization = Organization.create("우테코", "우테코입니다.", "image");

        return organizationRepository.save(organization);
    }

    private Member createMember() {
        return memberRepository.save(Member.create("name", "ahmadda@ahmadda.com"));
    }

    private Member createMember(String name, String email) {
        return memberRepository.save(Member.create(name, email));
    }

    private OrganizationMember createOrganizationMember(Organization organization, Member member) {
        var organizationMember = OrganizationMember.create("surf", member, organization);

        return organizationMemberRepository.save(organizationMember);
    }

    private Event createEvent(final OrganizationMember organizationMember, final Organization organization) {
        var now = LocalDateTime.now();

        var event = Event.create(
                "title",
                "description",
                "place",
                organizationMember,
                organization,
                EventOperationPeriod.create(
                        Period.create(now.plusDays(1), now.plusDays(2)),
                        Period.create(now.plusDays(3), now.plusDays(4)),
                        now
                ),
                "이벤트 근로",
                10
        );

        return eventRepository.save(event);
    }
}
