package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMember;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class OrganizationMemberServiceTest {

    @Autowired
    private OrganizationMemberService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @Test
    void 자신의_조직원_정보를_조회한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "hong@email.com", "testPicture"));
        var organization = organizationRepository.save(Organization.create("우테코", "설명", "img.png"));
        var organizationMember = organizationMemberRepository.save(
                OrganizationMember.create("닉네임", member, organization, Role.USER)
        );
        var loginMember = new LoginMember(member.getId());

        // when
        var result = sut.getOrganizationMember(organization.getId(), loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result.getId())
                    .isEqualTo(organizationMember.getId());
            softly.assertThat(result.getMember()
                            .getId())
                    .isEqualTo(member.getId());
            softly.assertThat(result.getOrganization()
                            .getId())
                    .isEqualTo(organization.getId());
        });
    }

    @Test
    void 존재하지_않는_조직원이면_예외가_발생한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "hong@email.com", "testPicture"));
        var organization = organizationRepository.save(Organization.create("우테코", "설명", "img.png"));
        var loginMember = new LoginMember(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.getOrganizationMember(organization.getId(), loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 조직원입니다.");
    }
}
