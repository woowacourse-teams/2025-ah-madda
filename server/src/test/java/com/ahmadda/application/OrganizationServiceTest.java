package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.dto.OrganizationUpdateRequest;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Event;
import com.ahmadda.domain.EventOperationPeriod;
import com.ahmadda.domain.EventRepository;
import com.ahmadda.domain.ImageFile;
import com.ahmadda.domain.InviteCode;
import com.ahmadda.domain.InviteCodeRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Role;
import com.ahmadda.presentation.dto.OrganizationParticipateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class OrganizationServiceTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private OrganizationService sut;

    @Test
    void 조직을_ID로_조회한다() {
        // given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);

        // when
        var found = sut.getOrganizationById(organization.getId());

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getName())
                    .isEqualTo("Org");
            softly.assertThat(found.getDescription())
                    .isEqualTo("Desc");
            softly.assertThat(found.getImageUrl())
                    .isEqualTo("img.png");
        });
    }

    @Test
    void 조직을_생성한다() {
        // given
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var request = createOrganizationCreateRequest("조직명", "조직 설명", "서프");
        var thumbnailImageFile = createImageFile("test.png");

        // when
        sut.createOrganization(request, thumbnailImageFile, new LoginMember(member.getId()));

        // then
        var organizations = organizationRepository.findAll();
        var organizationMembes = organizationMemberRepository.findAll();
        assertSoftly(softly -> {
            var organization = organizations.get(0);
            softly.assertThat(organizations)
                    .hasSize(1);
            softly.assertThat(organization.getName())
                    .isEqualTo("조직명");
            softly.assertThat(organization.getDescription())
                    .isEqualTo("조직 설명");
            softly.assertThat(organization.getImageUrl())
                    .isEqualTo("test.png");

            var organizationMember = organizationMembes.getFirst();
            softly.assertThat(organizationMembes)
                    .hasSize(1);
            softly.assertThat(organizationMember.getMember())
                    .isEqualTo(member);
            softly.assertThat(organizationMember.getOrganization())
                    .isEqualTo(organization);
            softly.assertThat(organizationMember.getNickname())
                    .isEqualTo("서프");
            softly.assertThat(organizationMember.getRole())
                    .isEqualTo(Role.ADMIN);
        });
    }

    @Test
    void 존재하지_않는_조직_ID로_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.getOrganizationById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }

    @Test
    void 존재하지_않는_조직의_이벤트를_조회하면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user", "user@test.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOrganizationEvents(999L, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }

    @Test
    void 여러_조직의_이벤트가_있을때_선택된_조직의_활성화된_이벤트만_가져온다() {
        // given
        var member = memberRepository.save(Member.create("name", "test@test.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());
        var orgA = organizationRepository.save(createOrganization("OrgA", "DescA", "a.png"));
        var orgB = organizationRepository.save(createOrganization("OrgB", "DescB", "b.png"));
        var orgMemberA =
                organizationMemberRepository.save(OrganizationMember.create("nickname", member, orgA, Role.USER));
        var orgMemberB =
                organizationMemberRepository.save(OrganizationMember.create("nickname", member, orgB, Role.USER));

        var now = LocalDateTime.now();
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA1", now.plusDays(1), now.plusDays(2)));
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA2", now.plusDays(2), now.plusDays(3)));
        eventRepository.save(createEvent(orgMemberA, orgA, "EventA3", now.minusDays(2), now.minusDays(1))); // inactive
        eventRepository.save(createEvent(orgMemberB, orgB, "EventB1", now.plusDays(1), now.plusDays(2)));

        // when
        var events = sut.getOrganizationEvents(orgA.getId(), loginMember);

        // then
        assertThat(events).hasSize(2)
                .extracting(Event::getTitle)
                .containsExactlyInAnyOrder("EventA1", "EventA2");
    }

    @Test
    void 조직원이_아니면_조직의_이벤트를_조회시_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user", "user@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOrganizationEvents(organization.getId(), loginMember))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("조직에 참여하지 않아 권한이 없습니다.");
    }

    @Test
    void 초대코드를_통해_조직에_참여할_수_있다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var inviter = createAndSaveOrganizationMember("surf", member2, organization, Role.USER);
        var inviteCode = createAndSaveInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode());

        // when
        var organizationMember = sut.participateOrganization(organization.getId(), loginMember, request);

        // then
        assertThat(organizationMemberRepository.findById(organizationMember.getId()))
                .isPresent()
                .hasValue(organizationMember);
    }

    @Test
    void 이미_참여한_조직에_중복_참여하면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var organizationMember = createAndSaveOrganizationMember("surf", member1, organization, Role.USER);
        var inviter = createAndSaveOrganizationMember("tuda", member2, organization, Role.USER);
        var inviteCode = createAndSaveInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("이미 참여한 조직입니다.");
    }

    @Test
    void 존재하지_않는_조직에_참여한다면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var inviter = createAndSaveOrganizationMember("surf", member2, organization, Role.USER);
        var inviteCode = createAndSaveInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(999L, loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }

    @Test
    void 존재하지_않는_회원이_조직에_참여하려_한다면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var inviter = createAndSaveOrganizationMember("surf", member, organization, Role.USER);
        var inviteCode = createAndSaveInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(999L);
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    @Test
    void 존재하지_않는_초대코드로_조직에_참여하려_한다면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));

        var loginMember = new LoginMember(member.getId());
        var request = new OrganizationParticipateRequest("new_nickname", "notFoundCode");

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(BusinessFlowViolatedException.class)
                .hasMessage("잘못된 초대코드입니다.");
    }

    @Test
    void DEPRECATED_항상_우아한코스_조직을_반환한다() {
        // when
        var woowacourse = sut.alwaysGetWoowacourse();

        // then
        assertThat(woowacourse.getName()).isEqualTo(OrganizationService.WOOWACOURSE_NAME);
    }

    @Test
    void DEPRECATED_여러번_요청해도_항상_우아한코스_조직을_반환한다() {
        //given
        Organization woowacourse =
                Organization.create(OrganizationService.WOOWACOURSE_NAME, "우아한테크코스입니당딩동", "imageUrl");
        organizationRepository.save(woowacourse);

        // when
        var getWoowacourse = sut.alwaysGetWoowacourse();

        // then
        assertThat(getWoowacourse.getName()).isEqualTo(OrganizationService.WOOWACOURSE_NAME);
    }

    @Test
    void 조직의_관리자는_조직을_수정할_수_있다() {
        //given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization, Role.ADMIN);
        var request = new OrganizationUpdateRequest("새 이름", "새 설명");
        var imageFile = createImageFile("new.png");

        //when
        sut.updateOrganization(organization.getId(), request, imageFile, new LoginMember(member.getId()));

        //then
        assertSoftly(softly -> {
            var updateOrganization = organizationRepository.findById(organization.getId())
                    .orElseThrow();
            softly.assertThat(updateOrganization.getName())
                    .isEqualTo("새 이름");
            softly.assertThat(updateOrganization.getDescription())
                    .isEqualTo("새 설명");
            softly.assertThat(updateOrganization.getImageUrl())
                    .isEqualTo("new.png");
        });
    }

    @Test
    void 썸네일이_null이어도_조직_수정이_가능하다() {
        //given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization, Role.ADMIN);
        var request = new OrganizationUpdateRequest("새 이름", "새 설명");

        //when
        sut.updateOrganization(organization.getId(), request, null, new LoginMember(member.getId()));

        //then
        assertSoftly(softly -> {
            var updateOrganization = organizationRepository.findById(organization.getId())
                    .orElseThrow();
            softly.assertThat(updateOrganization.getName())
                    .isEqualTo("새 이름");
            softly.assertThat(updateOrganization.getDescription())
                    .isEqualTo("새 설명");
            softly.assertThat(updateOrganization.getImageUrl())
                    .isEqualTo("img.png");
        });
    }

    @Test
    void 조직이_없다면_조직을_수정할때_예외가_발생한다() {
        // given
        var request = new OrganizationUpdateRequest("새 이름", "새 설명");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));

        // when // then
        assertThatThrownBy(() -> sut.updateOrganization(999L, request, null, new LoginMember(member.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }

    @Test
    void 조직원이_없다면_조직을_수정할때_예외가_발생한다() {
        // given
        var organization = createOrganization("Org", "Desc", "img.png");
        organizationRepository.save(organization);
        var request = new OrganizationUpdateRequest("새 이름", "새 설명");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));

        // when // then
        assertThatThrownBy(() -> sut.updateOrganization(
                organization.getId(),
                request,
                null,
                new LoginMember(member.getId())
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private Event createEvent(
            OrganizationMember organizer,
            Organization organization,
            String title,
            LocalDateTime start,
            LocalDateTime end
    ) {

        return Event.create(
                title,
                "description",
                "place",
                organizer,
                organization,
                EventOperationPeriod.create(
                        start, end,
                        end.plusHours(1), end.plusHours(2),
                        start.minusDays(1)
                ),
                100
        );
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(
            String name,
            String description,
            String nickname
    ) {
        return new OrganizationCreateRequest(name, description, nickname);
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            Member member,
            Organization organization,
            Role role
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, role);
        return organizationMemberRepository.save(organizationMember);
    }

    private InviteCode createAndSaveInviteCode(
            String code,
            Organization organization,
            OrganizationMember organizationMember,
            LocalDateTime now
    ) {
        InviteCode prevInviteCode = InviteCode.create(code, organization, organizationMember, now);
        return inviteCodeRepository.save(prevInviteCode);
    }

    private ImageFile createImageFile(String fileName) {
        return ImageFile.create(
                fileName,
                "image/png",
                1000,
                new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                }
        );
    }
}
