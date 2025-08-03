package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.InviteCode;
import com.ahmadda.domain.InviteCodeRepository;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.security.RandomCodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
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
    void 초대코드를_만들때_조직이_없다면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var organizationMember = createAndSaveOrganizationMember("surf", member, organization);
        var now = LocalDateTime.now();

        //when //then
        assertThatThrownBy(() -> sut.createInviteCode(999L, new LoginMember(member.getId()), now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직 정보입니다.");
    }

    @Test
    void 초대코드를_만들때_조직원이_없다면_예외가_발생한다() {
        //given
        var organization = createAndSaveOrganization("우테코");
        var member = createAndSaveMember("surf", "surf@ahmadda.com");
        var now = LocalDateTime.now();

        //when //then
        assertThatThrownBy(() -> sut.createInviteCode(organization.getId(), new LoginMember(member.getId()), now))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원 정보입니다.");
    }

    private Organization createAndSaveOrganization(String name) {
        var organization = Organization.create(name, "description", "imageUrl");
        return organizationRepository.save(organization);
    }

    private Member createAndSaveMember(String name, String email) {
        var member = Member.create(name, email);
        return memberRepository.save(member);
    }

    private OrganizationMember createAndSaveOrganizationMember(
            String nickname,
            Member member,
            Organization organization
    ) {
        var organizationMember = OrganizationMember.create(nickname, member, organization);
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
