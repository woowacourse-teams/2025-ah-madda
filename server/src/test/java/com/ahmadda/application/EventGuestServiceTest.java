package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.AnswerCreateRequest;
import com.ahmadda.application.dto.EventParticipateRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.Answer;
import com.ahmadda.domain.event.ApprovalStatus;
import com.ahmadda.domain.event.Event;
import com.ahmadda.domain.event.EventOperationPeriod;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.event.Guest;
import com.ahmadda.domain.event.GuestRepository;
import com.ahmadda.domain.event.Question;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
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

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 이벤트에_참여한_게스트들을_조회한다() {
        // given
        var organization = createAndSaveOrganization();
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember(
                        "주최자",
                        createAndSaveMember("홍길동", "host@email.com"),
                        organization,
                        group
                );
        var event = createAndSaveEvent(organizer, organization, false);

        var guest1 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember(
                        "게스트1",
                        createAndSaveMember("게스트1", "g1@email.com"),
                        organization,
                        group
                )
        );
        var guest2 = createAndSaveGuest(
                event,
                createAndSaveOrganizationMember(
                        "게스트2",
                        createAndSaveMember("게스트2", "g2@email.com"),
                        organization,
                        group
                )
        );

        // when
        var result = sut.getGuests(event.getId(), createLoginMember(organizer));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(guest1, guest2);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_게스트_조회시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember(
                        "주최자",
                        createAndSaveMember("주최자", "host@email.com"),
                        organization,
                        group
                );
        var loginMember = createLoginMember(organizer);

        // when // then
        assertThatThrownBy(() -> sut.getGuests(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 이벤트가_생성된_이벤트_스페이스의_구성원이_아닐때_게스트_조회시_예외가_발생한다() {
        // given
        var organization1 = createAndSaveOrganization();
        var organization2 = createAndSaveOrganization();
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember(
                        "주최자",
                        createAndSaveMember("홍길동", "host@email.com"),
                        organization1,
                        group
                );
        var otherMember =
                createAndSaveOrganizationMember(
                        "다른사람",
                        createAndSaveMember("user", "user@email.com"),
                        organization2,
                        group
                );
        var event = createAndSaveEvent(organizer, organization1, false);

        // when // then
        assertThatThrownBy(() -> sut.getGuests(event.getId(), createLoginMember(otherMember)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스의 구성원만 접근할 수 있습니다.");
    }

    @Test
    void 이벤트에_참여하지_않는_비게스트_구성원들을_조회한다() {
        // given
        var org = createAndSaveOrganization();
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember("주최자", createAndSaveMember("홍길동", "host@email.com"), org, group);
        var event = createAndSaveEvent(organizer, org, false);

        var guest = createAndSaveOrganizationMember("게스트", createAndSaveMember("게스트", "g@email.com"), org, group);
        var nonGuest1 =
                createAndSaveOrganizationMember("비게스트1", createAndSaveMember("비게스트1", "ng1@email.com"), org, group);
        var nonGuest2 =
                createAndSaveOrganizationMember("비게스트2", createAndSaveMember("비게스트2", "ng2@email.com"), org, group);
        createAndSaveGuest(event, guest);

        // when
        var result = sut.getNonGuestOrganizationMembers(event.getId(), createLoginMember(organizer));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(nonGuest1, nonGuest2);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_비게스트_조회시_예외가_발생한다() {
        // given
        var organization = organizationRepository.save(Organization.create("이벤트 스페이스명", "설명", "img.png"));
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember(
                        "주최자",
                        createAndSaveMember("주최자", "host@email.com"),
                        organization,
                        group
                );
        var loginMember = createLoginMember(organizer);

        // when // then
        assertThatThrownBy(() -> sut.getNonGuestOrganizationMembers(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 이벤트가_생성된_이벤트_스페이스의_구성원이_아닐때_비게스트_조회시_예외가_발생한다() {
        // given
        var organization1 = createAndSaveOrganization();
        var organization2 = createAndSaveOrganization();
        var group = createGroup();
        var organizer =
                createAndSaveOrganizationMember(
                        "주최자",
                        createAndSaveMember("홍길동", "host@email.com"),
                        organization1,
                        group
                );
        var otherMember =
                createAndSaveOrganizationMember(
                        "다른사람",
                        createAndSaveMember("user", "user@email.com"),
                        organization2,
                        group
                );
        var event = createAndSaveEvent(organizer, organization1, false);

        // when // then
        assertThatThrownBy(() -> sut.getNonGuestOrganizationMembers(event.getId(), createLoginMember(otherMember)))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스의 구성원만 접근할 수 있습니다.");
    }

    @Test
    void 그룹_내_비게스트_구성원들을_조회한다() {
        // given
        var organization = createAndSaveOrganization();
        var group1 = organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
        var group2 = organizationGroupRepository.save(OrganizationGroup.create("프론트엔드"));
        var organizer = createAndSaveOrganizationMember(
                "주최자",
                createAndSaveMember("홍길동", "host@email.com"), organization, group1
        );
        var event = createAndSaveEvent(organizer, organization);

        var guestInGroup1 = createAndSaveOrganizationMember(
                "게스트1",
                createAndSaveMember("게스트1", "g1@email.com"), organization, group1
        );
        var nonGuest1 = createAndSaveOrganizationMember(
                "비게스트1",
                createAndSaveMember("비게스트1", "ng1@email.com"), organization, group1
        );
        var nonGuest2 = createAndSaveOrganizationMember(
                "비게스트2",
                createAndSaveMember("비게스트2", "ng2@email.com"), organization, group1
        );
        var otherGroupMember = createAndSaveOrganizationMember(
                "프론트",
                createAndSaveMember("프론트", "fe@email.com"), organization, group2
        );
        createAndSaveGuest(event, guestInGroup1);

        // when
        var result =
                sut.getGroupNonGuestOrganizationMembers(event.getId(), group1.getId(), createLoginMember(organizer));

        // then
        assertSoftly(softly -> {
            softly.assertThat(result)
                    .hasSize(2);
            softly.assertThat(result)
                    .containsExactlyInAnyOrder(nonGuest1, nonGuest2);
            softly.assertThat(result)
                    .doesNotContain(guestInGroup1, otherGroupMember);
        });
    }

    @Test
    void 존재하지_않는_이벤트로_비게스트를_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var group = createGroup();
        var organizationMember = createAndSaveOrganizationMember(
                "주최자",
                createAndSaveMember("홍길동", "host@email.com"), organization, group
        );

        // when // then
        assertThatThrownBy(() ->
                sut.getGroupNonGuestOrganizationMembers(999L, group.getId(), createLoginMember(organizationMember))
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 이벤트_스페이스의_구성원이_아닐때_그룹_비게스트_조회시_예외가_발생한다() {
        // given
        var organization1 = createAndSaveOrganization();
        var organization2 = createAndSaveOrganization();
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember(
                "주최자",
                createAndSaveMember("홍길동", "host@email.com"), organization1, group
        );
        var outsider = createAndSaveOrganizationMember(
                "다른사람",
                createAndSaveMember("외부", "other@email.com"), organization2, group
        );
        var event = createAndSaveEvent(organizer, organization1);

        // when // then
        assertThatThrownBy(() ->
                sut.getGroupNonGuestOrganizationMembers(event.getId(), group.getId(), createLoginMember(outsider))
        )
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스의 구성원만 접근할 수 있습니다.");
    }

    @Test
    void 존재하지_않는_그룹으로_비게스트를_조회하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember(
                "주최자",
                createAndSaveMember("홍길동", "host@email.com"), organization, group
        );
        var event = createAndSaveEvent(organizer, organization);

        // when // then
        assertThatThrownBy(() ->
                sut.getGroupNonGuestOrganizationMembers(event.getId(), 999L, createLoginMember(organizer))
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 그룹입니다.");
    }

    @Test
    void 구성원은_이벤트의_게스트로_참여할_수_있다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var group = createGroup();
        var organizationMember1 = createAndSaveOrganizationMember("surf1", member1, organization, group);
        var organizationMember2 = createAndSaveOrganizationMember("surf2", member2, organization, group);
        var event = createAndSaveEvent(organizationMember1, organization, false);

        // when
        sut.participantEvent(
                event.getId(),
                new LoginMember(member2.getId()),
                event.getRegistrationStart(),
                new EventParticipateRequest(List.of())
        );

        // then
        var guests = guestRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(guests)
                    .hasSize(1);
            var guest = guests.getFirst();
            softly.assertThat(guest.getEvent())
                    .isEqualTo(event);
            softly.assertThat(guest.getOrganizationMember())
                    .isEqualTo(organizationMember2);
        });
    }

    @Test
    void 필수_질문에_모두_답변하면_참여할_수_있다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization, group);
        var participant = createAndSaveOrganizationMember("parti", member2, organization, group);

        var question1 = Question.create("필수 질문", true, 0);
        var question2 = Question.create("선택 질문", false, 1);
        var event = createAndSaveEvent(organizer, organization, false, question1, question2);


        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(question1.getId(), "답변1"),
                new AnswerCreateRequest(question2.getId(), "답변2")
        ));

        // when
        sut.participantEvent(event.getId(), new LoginMember(member2.getId()), event.getRegistrationStart(), request);

        // then
        var guest = guestRepository.findAll()
                .getFirst();
        assertSoftly(softly -> {
            softly.assertThat(guest.getAnswers())
                    .hasSize(2);
            softly.assertThat(guest.getAnswers())
                    .extracting(Answer::getAnswerText)
                    .containsExactlyInAnyOrder("답변1", "답변2");
        });
    }

    @Test
    void 필수_질문에_대한_답변이_누락되면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization, group);
        var participant = createAndSaveOrganizationMember("parti", member2, organization, group);

        Question question1 = Question.create("필수 질문", true, 0);
        Question question2 = Question.create("선택 질문", false, 1);
        var event = createAndSaveEvent(
                organizer,
                organization, false,
                question1,
                question2
        );

        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(question2.getId(), "선택 답변")
        ));

        // when // then
        assertThatThrownBy(() ->
                sut.participantEvent(
                        event.getId(),
                        new LoginMember(member2.getId()),
                        event.getRegistrationStart(),
                        request
                )
        )
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessageContaining("필수 질문에 대한 답변이 누락되었습니다");
    }

    @Test
    void 존재하지_않는_질문에_답변하면_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("name1", "email1@ahmadda.com");
        var member2 = createAndSaveMember("name2", "email2@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("organizer", member1, organization, group);
        var participant = createAndSaveOrganizationMember("parti", member2, organization, group);
        var event = createAndSaveEvent(organizer, organization, false);

        var invalidQuestionId = 999L;

        var request = new EventParticipateRequest(List.of(
                new AnswerCreateRequest(invalidQuestionId, "답변")
        ));

        // when // then
        assertThatThrownBy(() ->
                sut.participantEvent(
                        event.getId(),
                        new LoginMember(member2.getId()),
                        event.getRegistrationStart(),
                        request
                )
        )
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 질문입니다.");
    }

    @Test
    void 특정_구성원이_이벤트의_게스트인지_알_수_있다() {
        //given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var member3 = createAndSaveMember("test3", "ahmadda3@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);
        var organizationMember3 =
                createAndSaveOrganizationMember("om2", member3, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, false);
        createAndSaveGuest(event, organizationMember2);

        //when
        var actual1 = sut.isGuest(event.getId(), createLoginMember(organizationMember2));
        var actual2 = sut.isGuest(event.getId(), createLoginMember(organizationMember3));

        //then
        assertThat(actual1).isEqualTo(true);
        assertThat(actual2).isEqualTo(false);
    }

    @Test
    void 구성원이_아니라면_게스트_여부를_확인할때_예외가_발생한다() {
        // given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var member3 = createAndSaveMember("test3", "ahmadda3@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, false);
        createAndSaveGuest(event, organizationMember2);

        // when // then
        assertThatThrownBy(() -> sut.isGuest(event.getId(), new LoginMember(member3.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 주최자는_게스트의_답변을_볼_수_있다() {
        //given
        var organization = createAndSaveOrganization();
        var organizerMember = createAndSaveMember("이재훈", "surf@ahmadda.com");
        var guestMember = createAndSaveMember("머피", "mpi@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("surf1", organizerMember, organization, group);
        var organizationMember = createAndSaveOrganizationMember("mpi", guestMember, organization, group);
        var question1 = Question.create("1", true, 1);
        var question2 = Question.create("2", true, 2);
        var event = createAndSaveEvent(
                organizer,
                organization, false,
                question1,
                question2
        );
        var guest = createAndSaveGuest(event, organizationMember);
        guest.submitAnswers(Map.of(
                question1, "answer1",
                question2, "answer2"
        ));

        //when
        var answers = sut.getAnswers(event.getId(), guest.getId(), new LoginMember(organizerMember.getId()));

        //then
        assertSoftly(softly -> {
            softly.assertThat(answers)
                    .hasSize(2);
            softly.assertThat(answers)
                    .extracting("answerText")
                    .contains("answer1", "answer2");
        });
    }

    @Test
    void 이벤트가_없다면_게스트의_답변을_볼때_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization();
        var organizerMember = createAndSaveMember("이재훈", "surf@ahmadda.com");

        //when //then
        assertThatThrownBy(() -> sut.getAnswers(999L, 1L, new LoginMember(organizerMember.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트입니다.");
    }

    @Test
    void 주최자가_없다면_게스트의_답변을_볼때_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization();
        var organizerMember = createAndSaveMember("이재훈", "surf@ahmadda.com");
        var guestMember = createAndSaveMember("머피", "mpi@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("surf1", organizerMember, organization, group);
        var organizationMember = createAndSaveOrganizationMember("mpi", guestMember, organization, group);
        var question1 = Question.create("1", true, 1);
        var question2 = Question.create("2", true, 2);
        var event = createAndSaveEvent(
                organizer,
                organization, false,
                question1,
                question2
        );
        var guest = createAndSaveGuest(event, organizationMember);
        guest.submitAnswers(Map.of(
                question1, "answer1",
                question2, "answer2"
        ));

        //when //then
        assertThatThrownBy(() -> sut.getAnswers(event.getId(), 1L, new LoginMember(999L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 게스트가_없다면_게스트의_답변을_볼때_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization();
        var organizerMember = createAndSaveMember("이재훈", "surf@ahmadda.com");
        var group = createGroup();
        var organizer = createAndSaveOrganizationMember("surf1", organizerMember, organization, group);
        var question1 = Question.create("1", true, 1);
        var question2 = Question.create("2", true, 2);
        var event = createAndSaveEvent(
                organizer,
                organization, false,
                question1,
                question2
        );

        //when //then
        assertThatThrownBy(() -> sut.getAnswers(event.getId(), 999L, new LoginMember(organizerMember.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 게스트입니다.");
    }

    @Test
    void 게스트를_승인할_수_있다() {
        //given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, true);
        Guest guest = createAndSaveGuest(event, organizationMember2);

        //when
        sut.receiveApprovalFromOrganizer(event.getId(), guest.getId(), new LoginMember(member1.getId()));

        //then
        assertThat(guest.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }

    @Test
    void 주최자가_아닌_다른_구성원이_게스트를_승인하면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var member3 = createAndSaveMember("test3", "ahmadda3@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);
        var organizationMember3 =
                createAndSaveOrganizationMember("om3", member3, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, true);
        Guest guest = createAndSaveGuest(event, organizationMember2);

        //when //then
        assertThatThrownBy(() -> sut.receiveApprovalFromOrganizer(
                event.getId(),
                guest.getId(),
                new LoginMember(member3.getId())
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("주최자만 게스트를 승인할 수 있습니다.");
    }

    @Test
    void 게스트를_거절할_수_있다() {
        //given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, true);
        Guest guest = createAndSaveGuest(event, organizationMember2);

        //when
        sut.receiveRejectFromOrganizer(event.getId(), guest.getId(), new LoginMember(member1.getId()));

        //then
        assertThat(guest.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
    }

    @Test
    void 주최자가_아닌_다른_구성원이_게스트를_거절하면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization();
        var member1 = createAndSaveMember("test1", "ahmadda1@ahmadda.com");
        var member2 = createAndSaveMember("test2", "ahmadda2@ahmadda.com");
        var member3 = createAndSaveMember("test3", "ahmadda3@ahmadda.com");
        var group = createGroup();
        var organizationMember1 =
                createAndSaveOrganizationMember("om1", member1, organization, group);
        var organizationMember2 =
                createAndSaveOrganizationMember("om2", member2, organization, group);
        var organizationMember3 =
                createAndSaveOrganizationMember("om3", member3, organization, group);

        var event = createAndSaveEvent(organizationMember1, organization, true);
        Guest guest = createAndSaveGuest(event, organizationMember2);

        //when //then
        assertThatThrownBy(() -> sut.receiveRejectFromOrganizer(
                event.getId(),
                guest.getId(),
                new LoginMember(member3.getId())
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("주최자만 게스트를 거절할 수 있습니다.");
    }

    private Member createAndSaveMember(String name, String email) {
        return memberRepository.save(Member.create(name, email, "testPicture"));
    }

    private Organization createAndSaveOrganization() {
        return organizationRepository.save(Organization.create("이벤트 스페이스", "설명", "img.png"));
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            Member member,
            Organization org,
            OrganizationGroup group
    ) {
        return organizationMemberRepository.save(OrganizationMember.create(
                nickname,
                member,
                org,
                OrganizationMemberRole.USER,
                group
        ));
    }

    private Event createAndSaveEvent(
            OrganizationMember organizer,
            Organization organization,
            boolean isApprovalRequired,
            Question... questions
    ) {
        var now = LocalDateTime.now();
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        now.minusDays(3), now.minusDays(1),
                        now.plusDays(1), now.plusDays(2),
                        now.minusDays(6)
                ),
                100,
                isApprovalRequired,
                questions
        );

        return eventRepository.save(event);
    }

    private Event createAndSaveEventWithTime(
            OrganizationMember organizer,
            Organization organization,
            LocalDateTime registrationStart,
            LocalDateTime registrationEnd,
            LocalDateTime eventStart,
            LocalDateTime eventEnd,
            Question... questions
    ) {
        var creationTime = registrationStart.minusDays(1);
        var event = Event.create(
                "이벤트",
                "설명",
                "장소",
                organizer,
                organization,
                EventOperationPeriod.create(
                        registrationStart,
                        registrationEnd,
                        eventStart,
                        eventEnd,
                        creationTime
                ),
                100,
                false,
                questions
        );

        return eventRepository.save(event);
    }

    private Guest createAndSaveGuest(Event event, OrganizationMember member) {
        return guestRepository.save(Guest.create(event, member, event.getRegistrationStart()));
    }

    private LoginMember createLoginMember(OrganizationMember organizationMember) {
        var member = organizationMember.getMember();

        return new LoginMember(member.getId());
    }

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
