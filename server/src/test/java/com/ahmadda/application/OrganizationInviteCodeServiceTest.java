package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnprocessableEntityException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.InviteCode;
import com.ahmadda.domain.organization.InviteCodeRepository;
import com.ahmadda.domain.organization.Organization;
import com.ahmadda.domain.organization.OrganizationMember;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRole;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.domain.organization.RandomCodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class OrganizationInviteCodeServiceTest {

    @Autowired
    private OrganizationInviteCodeService sut;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;
    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Test
    void 여섯_글자의_초대코드를_생성한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var now = LocalDateTime.now();

        //when
        var inviteCode = sut.createInviteCode(organization.getId(), new LoginMember(member.getId()), now);

        //then
        assertSoftly(softly -> {
            softly.assertThat(inviteCodeRepository.findById(inviteCode.getId()))
                    .isPresent()
                    .hasValue(inviteCode);
            softly.assertThat(inviteCode.getInviter())
                    .isEqualTo(organizationMember);
            softly.assertThat(inviteCode.getOrganization())
                    .isEqualTo(organization);
            softly.assertThat(inviteCode.getCode())
                    .isEqualTo("aaaaaa");
        });
    }

    @Test
    void 초대코드_생성시_만료전인_초대코드가_있다면_해당_초대코드를_반환한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var prevInviteCodeCreateDateTime = LocalDateTime.of(2025, 7, 1, 0, 0);
        var prevInviteCode =
                createAndSaveInviteCode("ahmada", organization, organizationMember, prevInviteCodeCreateDateTime);
        var now = LocalDateTime.of(2025, 7, 7, 23, 59);

        //when
        var inviteCode = sut.createInviteCode(organization.getId(), new LoginMember(member.getId()), now);

        //then
        assertThat(inviteCode).isEqualTo(prevInviteCode);
    }

    @Test
    void 초대코드_생성시_만료된_초대코드만_있다면_새로운_초대코드를_만든다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var prevInviteCodeCreateDateTime = LocalDateTime.of(2025, 7, 1, 0, 0);
        var prevInviteCode =
                createAndSaveInviteCode("ahmada", organization, organizationMember, prevInviteCodeCreateDateTime);
        var now = LocalDateTime.of(2025, 7, 8, 0, 1);

        //when
        var inviteCode = sut.createInviteCode(organization.getId(), new LoginMember(member.getId()), now);

        //then
        assertSoftly(softly -> {
            softly.assertThat(inviteCode)
                    .isNotEqualTo(prevInviteCode);
            softly.assertThat(inviteCodeRepository.findById(inviteCode.getId()))
                    .isPresent()
                    .hasValue(inviteCode);
            softly.assertThat(inviteCode.getInviter())
                    .isEqualTo(organizationMember);
            softly.assertThat(inviteCode.getOrganization())
                    .isEqualTo(organization);
            softly.assertThat(inviteCode.getCode())
                    .isEqualTo("aaaaaa");
        });
    }

    @Test
    void 초대코드를_만들때_이벤트_스페이스가_없다면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var now = LocalDateTime.now();

        //when //then
        assertThatThrownBy(() -> sut.createInviteCode(999L, new LoginMember(member.getId()), now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 이벤트 스페이스 정보입니다.");
    }

    @Test
    void 초대코드를_만들때_구성원이_없다면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var now = LocalDateTime.now();

        //when //then
        assertThatThrownBy(() -> sut.createInviteCode(organization.getId(), new LoginMember(member.getId()), now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 구성원 정보입니다.");
    }

    @Test
    void 초대코드를_통해_이벤트_스페이스를_조회할_수_있다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var inviteCode = createAndSaveInviteCode("ahmada", organization, organizationMember, LocalDateTime.now());

        //when
        var findOrganization = sut.getOrganizationByCode(inviteCode.getCode());

        //then
        assertThat(findOrganization).isEqualTo(organization);
    }

    @Test
    void 존재하지_않는_초대코드로_이벤트_스페이스를_찾는다면_예외가_발생한다() {
        //when //then
        assertThatThrownBy(() -> sut.getOrganizationByCode("fakeCode"))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("유효하지 않은 초대코드입니다.");
    }

    @Test
    void 만료된_초대코드를_통해_이벤트_스페이스를_조회한다면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var prevInviteCodeCreateDateTime = LocalDateTime.of(2025, 7, 1, 0, 0);
        var inviteCode =
                createAndSaveInviteCode("ahmada", organization, organizationMember, prevInviteCodeCreateDateTime);

        //when //then
        assertThatThrownBy(() -> sut.getOrganizationByCode(inviteCode.getCode()))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage("만료된 초대코드입니다.");
    }

    private Organization createAndSaveOrganization(String name) {
        var organization = Organization.create(name, "description", "imageUrl");
        return organizationRepository.save(organization);
    }

    private Member createAndSaveMember(String name, String email) {
        var member = Member.create(name, email, "testPicture");
        return memberRepository.save(member);
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            Member member,
            Organization organization
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization, OrganizationMemberRole.USER);
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

    @TestConfiguration
    static class OrganizationInviteCodeServiceTestContextConfiguration {

        @Bean
        public RandomCodeGenerator randomCodeGenerator() {
            return length -> {
                StringBuilder sb = new StringBuilder();
                sb.repeat('a', length);
                return sb.toString();
            };
        }
    }
}
