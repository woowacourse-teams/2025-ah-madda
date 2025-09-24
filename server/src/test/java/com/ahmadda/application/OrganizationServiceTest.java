package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.OrganizationCreateRequest;
import com.ahmadda.application.dto.OrganizationUpdateRequest;
import com.ahmadda.common.exception.ForbiddenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.event.EventRepository;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.InviteCode;
import com.ahmadda.domain.organization.InviteCodeRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.domain.organization.OrganizationImageFile;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.presentation.dto.OrganizationParticipateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
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

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

    @Test
    void 이벤트_스페이스를_ID로_조회한다() {
        // given
        var organization = createOrganization("Org");

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
    void 이벤트_스페이스를_생성한다() {
        // given
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var group = createGroup();
        var request = createOrganizationCreateRequest("이벤트 스페이스명", "이벤트 스페이스 설명", "서프", group.getId());
        var thumbnailImageFile = createImageFile("test.png");

        // when
        sut.createOrganization(request, thumbnailImageFile, new LoginMember(member.getId()));

        // then
        var organizations = organizationRepository.findAll();
        var organizationMembers = organizationMemberRepository.findAll();
        assertSoftly(softly -> {
            var organization = organizations.get(0);
            softly.assertThat(organizations)
                    .hasSize(1);
            softly.assertThat(organization.getName())
                    .isEqualTo("이벤트 스페이스명");
            softly.assertThat(organization.getDescription())
                    .isEqualTo("이벤트 스페이스 설명");
            softly.assertThat(organization.getImageUrl())
                    .isEqualTo("test.png");

            var organizationMember = organizationMembers.getFirst();
            softly.assertThat(organizationMembers)
                    .hasSize(1);
            softly.assertThat(organizationMember.getMember())
                    .isEqualTo(member);
            softly.assertThat(organizationMember.getOrganization())
                    .isEqualTo(organization);
            softly.assertThat(organizationMember.getNickname())
                    .isEqualTo("서프");
            softly.assertThat(organizationMember.getRole())
                    .isEqualTo(OrganizationMemberRole.ADMIN);
            softly.assertThat(organizationMember.getGroup())
                    .isEqualTo(group);
        });
    }

    @Test
    void 이벤트_스페이스를_생성시_존재하지_않는_그룹으로_생성하면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var request = createOrganizationCreateRequest("이벤트 스페이스명", "이벤트 스페이스 설명", "서프", 999L);
        var thumbnailImageFile = createImageFile("test.png");

        // when // then
        assertThatThrownBy(() -> sut.createOrganization(request, thumbnailImageFile, new LoginMember(member.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 그룹입니다.");
    }

    @Test
    void 존재하지_않는_이벤트_스페이스_ID로_조회하면_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> sut.getOrganizationById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 초대코드를_통해_이벤트_스페이스에_참여할_수_있다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var inviter = createOrganizationMember("surf", member2, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("nickname", inviteCode.getCode(), group.getId());

        // when
        var organizationMember = sut.participateOrganization(organization.getId(), loginMember, request);

        // then
        assertThat(organizationMemberRepository.findById(organizationMember.getId()))
                .isPresent()
                .hasValue(organizationMember);
    }

    @Test
    void 이미_참여한_이벤트_스페이스에_중복_참여하면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var organizationMember =
                createOrganizationMember("surf", member1, organization, OrganizationMemberRole.USER, group);
        var inviter = createOrganizationMember("tuda", member2, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode(), group.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미 참여한 이벤트 스페이스입니다.");
    }

    @Test
    void 존재하지_않는_그룹으로_이벤트_스페이스에_참여한다면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var inviter = createOrganizationMember("tuda", member2, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode(), 999L);

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 그룹입니다.");
    }

    @Test
    void 존재하지_않는_이벤트_스페이스에_참여한다면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var inviter = createOrganizationMember("surf", member2, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode(), group.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(999L, loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 존재하지_않는_회원이_이벤트_스페이스에_참여하려_한다면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var inviter = createOrganizationMember("surf", member, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(999L);
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode(), group.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    @Test
    void 존재하지_않는_초대코드로_이벤트_스페이스에_참여하려_한다면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));

        var group = createGroup();
        var loginMember = new LoginMember(member.getId());
        var request = new OrganizationParticipateRequest("new_nickname", "notFoundCode", group.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("잘못된 초대코드입니다.");
    }

    @Test
    void 이벤트_스페이스의_관리자는_이벤트_스페이스를_수정할_수_있다() {
        //given
        var organization = createOrganization("Org");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var group = createGroup();
        var organizationMember =
                createOrganizationMember("surf", member, organization, OrganizationMemberRole.ADMIN, group);
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
    void 썸네일이_null이어도_이벤트_스페이스_수정이_가능하다() {
        //given
        var organization = createOrganization("Org");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var group = createGroup();
        var organizationMember =
                createOrganizationMember("surf", member, organization, OrganizationMemberRole.ADMIN, group);
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
    void 이벤트_스페이스가_없다면_이벤트_스페이스를_수정할때_예외가_발생한다() {
        // given
        var request = new OrganizationUpdateRequest("새 이름", "새 설명");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));

        // when // then
        assertThatThrownBy(() -> sut.updateOrganization(999L, request, null, new LoginMember(member.getId())))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 구성원이_없다면_이벤트_스페이스를_수정할때_예외가_발생한다() {
        // given
        var organization = createOrganization("Org");
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
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 사용자가_가입한_이벤트_스페이스를_조회할_수_있다() {
        //given
        var organization1 = createOrganization("우테코");
        var organization2 = createOrganization("아맞다");
        var organization3 = createOrganization("서프의 이벤트 스페이스");
        var organization4 = createOrganization("프론트 이벤트 스페이스");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var group = createGroup();
        createOrganizationMember("surf", member, organization1, OrganizationMemberRole.USER, group);
        createOrganizationMember("surf", member, organization2, OrganizationMemberRole.ADMIN, group);
        createOrganizationMember("surf", member, organization3, OrganizationMemberRole.USER, group);

        //when
        var participatingOrganizations =
                sut.getParticipatingOrganizations(new LoginMember(member.getId()));

        //then
        assertSoftly(softly -> {
            softly.assertThat(participatingOrganizations)
                    .hasSize(3);
            softly.assertThat(participatingOrganizations)
                    .extracting("name")
                    .contains("우테코", "아맞다", "서프의 이벤트 스페이스");
        });
    }

    @Test
    void 사용자가_가입한_이벤트_스페이스를_조회할때_사용자가_없다면_예외가_발생한다() {
        //when //then
        assertThatThrownBy(() -> sut.getParticipatingOrganizations(new LoginMember(999L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    @Test
    void 이벤트_스페이스의_관리자는_이벤트_스페이스를_삭제할_수_있다() {
        // given
        var organization = createOrganization("삭제될 이벤트 스페이스");
        var admin = memberRepository.save(Member.create("admin", "admin@test.com", "pic"));
        var group = createGroup();
        createOrganizationMember("관리자", admin, organization, OrganizationMemberRole.ADMIN, group);

        var loginMember = new LoginMember(admin.getId());

        // when
        sut.deleteOrganization(organization.getId(), loginMember);

        // then
        assertThat(organizationRepository.findById(organization.getId())).isEmpty();
    }

    @Test
    void 이벤트_스페이스_삭제시_이벤트_스페이스가_존재하지_않는다면_예외가_발생한다() {
        // given
        var invalidId = Long.MAX_VALUE;
        var loginMember = new LoginMember(1L);

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(invalidId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스입니다.");
    }

    @Test
    void 이벤트_스페이스_삭제시_이벤트_스페이스에_속하지_않는_회원이라면_예외가_발생한다() {
        // given
        var organization = createOrganization("삭제 대상 이벤트 스페이스");
        var member = memberRepository.save(Member.create("user", "user@test.com", "pic"));
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(organization.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 이벤트_스페이스_삭제시_이벤트_스페이스의_관리자가_아니라면_예외가_발생한다() {
        // given
        var organization = createOrganization("삭제 불가 이벤트 스페이스");
        var user = memberRepository.save(Member.create("user", "user@test.com", "pic"));
        var group = createGroup();
        createOrganizationMember("사용자", user, organization, OrganizationMemberRole.USER, group);

        var loginMember = new LoginMember(user.getId());

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(organization.getId(), loginMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("이벤트 스페이스의 관리자만 삭제할 수 있습니다.");
    }

    @Test
    void 이벤트_스페이스에_참여시_이미_정원이_가득_찼으면_예외가_발생한다() {
        // given
        var organization = createOrganization("이벤트 스페이스");
        var organizer = memberRepository.save(Member.create("organizer", "organizer@test.com", "pic"));
        var group = createGroup();
        var organizationMember = OrganizationMember.create(
                "organizer",
                organizer,
                organization,
                OrganizationMemberRole.ADMIN,
                group
        );
        organizationMemberRepository.save(organizationMember);

        var inviteCode = createInviteCode("code", organization, organizationMember, LocalDateTime.now());

        for (int i = 0; i < 299; i++) {
            var user = memberRepository.save(Member.create("user", i + "user@test.com", "pic"));
            var loginMember = new LoginMember(user.getId());

            sut.participateOrganization(
                    organization.getId(),
                    loginMember,
                    new OrganizationParticipateRequest("parti" + i, inviteCode.getCode(), group.getId())
            );
        }

        var cannotParticipateUser =
                memberRepository.save(Member.create("cannotparticipate", "cannotparticiapteuser@test.com", "pic"));
        var loginMember = new LoginMember(cannotParticipateUser.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(
                organization.getId(),
                loginMember,
                new OrganizationParticipateRequest("cannotpar", inviteCode.getCode(), group.getId())
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이벤트 스페이스에 이미 정원이 가득차 참여할 수 없습니다.");
    }

    @Test
    void 이미_같은_이름의_사용자가_존재한다면_가입하려하면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var group = createGroup();
        var inviter = createOrganizationMember("surf", member2, organization, OrganizationMemberRole.USER, group);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("newname", inviteCode.getCode(), group.getId());

        sut.participateOrganization(organization.getId(), loginMember, request);

        var duplicateNameMember =
                memberRepository.save(Member.create("dupliName", "user3@test.com", "testPicture"));
        var duplicateName = "surf";
        var duplicateNameRequest =
                new OrganizationParticipateRequest(duplicateName, inviteCode.getCode(), group.getId());
        var duplicateLoginMember = new LoginMember(duplicateNameMember.getId());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(
                organization.getId(),
                duplicateLoginMember,
                duplicateNameRequest
        ))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    private Organization createOrganization(String name) {
        var organization = createOrganization(name, "Desc", "img.png");
        return organizationRepository.save(organization);
    }

    private Organization createOrganization(String name, String description, String imageUrl) {
        return Organization.create(name, description, imageUrl);
    }

    private OrganizationCreateRequest createOrganizationCreateRequest(
            String name,
            String description,
            String nickname,
            Long groupId
    ) {
        return new OrganizationCreateRequest(name, description, nickname, groupId);
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization organization,
            OrganizationMemberRole role,
            OrganizationGroup group
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, role, group);
        return organizationMemberRepository.save(organizationMember);
    }

    private InviteCode createInviteCode(
            String code,
            Organization organization,
            OrganizationMember organizationMember,
            LocalDateTime now
    ) {
        InviteCode prevInviteCode = InviteCode.create(code, organization, organizationMember, now);
        return inviteCodeRepository.save(prevInviteCode);
    }

    private OrganizationImageFile createImageFile(String fileName) {
        return OrganizationImageFile.create(
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

    private OrganizationGroup createGroup() {
        return organizationGroupRepository.save(OrganizationGroup.create("백엔드"));
    }
}
