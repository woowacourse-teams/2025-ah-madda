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

    @Test
    void 조직을_ID로_조회한다() {
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
                    .isEqualTo(OrganizationMemberRole.ADMIN);
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
    void 초대코드를_통해_조직에_참여할_수_있다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var inviter = createOrganizationMember("surf", member2, organization, OrganizationMemberRole.USER);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

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
        var organizationMember =
                createOrganizationMember("surf", member1, organization, OrganizationMemberRole.USER);
        var inviter = createOrganizationMember("tuda", member2, organization, OrganizationMemberRole.USER);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

        var loginMember = new LoginMember(member1.getId());
        var request = new OrganizationParticipateRequest("new_nickname", inviteCode.getCode());

        // when // then
        assertThatThrownBy(() -> sut.participateOrganization(organization.getId(), loginMember, request))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("이미 참여한 조직입니다.");
    }

    @Test
    void 존재하지_않는_조직에_참여한다면_예외가_발생한다() {
        // given
        var member1 = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var member2 = memberRepository.save(Member.create("user2", "user2@test.com", "testPicture"));
        var organization = organizationRepository.save(createOrganization("Org", "Desc", "img.png"));
        var inviter = createOrganizationMember("surf", member2, organization, OrganizationMemberRole.USER);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

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
        var inviter = createOrganizationMember("surf", member, organization, OrganizationMemberRole.USER);
        var inviteCode = createInviteCode("code", organization, inviter, LocalDateTime.now());

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
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("잘못된 초대코드입니다.");
    }

    @Test
    void 조직의_관리자는_조직을_수정할_수_있다() {
        //given
        var organization = createOrganization("Org");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organizationMember =
                createOrganizationMember("surf", member, organization, OrganizationMemberRole.ADMIN);
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
        var organization = createOrganization("Org");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        var organizationMember =
                createOrganizationMember("surf", member, organization, OrganizationMemberRole.ADMIN);
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
    void 구성원이_없다면_조직을_수정할때_예외가_발생한다() {
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
    void 사용자가_가입한_조직을_조회할_수_있다() {
        //given
        var organization1 = createOrganization("우테코");
        var organization2 = createOrganization("아맞다");
        var organization3 = createOrganization("서프의 조직");
        var organization4 = createOrganization("프론트 조직");
        var member = memberRepository.save(Member.create("user1", "user1@test.com", "testPicture"));
        createOrganizationMember("surf", member, organization1, OrganizationMemberRole.USER);
        createOrganizationMember("surf", member, organization2, OrganizationMemberRole.ADMIN);
        createOrganizationMember("surf", member, organization3, OrganizationMemberRole.USER);

        //when
        var participatingOrganizations =
                sut.getParticipatingOrganizations(new LoginMember(member.getId()));

        //then
        assertSoftly(softly -> {
            softly.assertThat(participatingOrganizations)
                    .hasSize(3);
            softly.assertThat(participatingOrganizations)
                    .extracting("name")
                    .contains("우테코", "아맞다", "서프의 조직");
        });
    }

    @Test
    void 사용자가_가입한_조직을_조회할때_사용자가_없다면_예외가_발생한다() {
        //when //then
        assertThatThrownBy(() -> sut.getParticipatingOrganizations(new LoginMember(999L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    @Test
    void 조직의_관리자는_조직을_삭제할_수_있다() {
        // given
        var organization = createOrganization("삭제될 조직");
        var admin = memberRepository.save(Member.create("admin", "admin@test.com", "pic"));
        createOrganizationMember("관리자", admin, organization, OrganizationMemberRole.ADMIN);

        var loginMember = new LoginMember(admin.getId());

        // when
        sut.deleteOrganization(organization.getId(), loginMember);

        // then
        assertThat(organizationRepository.findById(organization.getId())).isEmpty();
    }

    @Test
    void 조직_삭제시_조직이_존재하지_않는다면_예외가_발생한다() {
        // given
        var invalidId = Long.MAX_VALUE;
        var loginMember = new LoginMember(1L);

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(invalidId, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직입니다.");
    }

    @Test
    void 조직_삭제시_조직에_속하지_않은_회원이라면_예외가_발생한다() {
        // given
        var organization = createOrganization("삭제 대상 조직");
        var member = memberRepository.save(Member.create("user", "user@test.com", "pic"));
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(organization.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원입니다.");
    }

    @Test
    void 조직_삭제시_조직의_관리자가_아니라면_예외가_발생한다() {
        // given
        var organization = createOrganization("삭제 불가 조직");
        var user = memberRepository.save(Member.create("user", "user@test.com", "pic"));
        createOrganizationMember("사용자", user, organization, OrganizationMemberRole.USER);

        var loginMember = new LoginMember(user.getId());

        // when // then
        assertThatThrownBy(() -> sut.deleteOrganization(organization.getId(), loginMember))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("조직의 관리자만 삭제할 수 있습니다.");
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
            String nickname
    ) {
        return new OrganizationCreateRequest(name, description, nickname);
    }

    private OrganizationMember createOrganizationMember(
            String nickname,
            Member member,
            Organization organization,
            OrganizationMemberRole role
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, role);
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
}
