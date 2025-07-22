package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.Organization;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.jwt.JwtTokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class LoginServiceTest {

    @Autowired
    private LoginService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @MockitoBean
    private GoogleOAuthProvider googleOAuthProvider;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 신규회원이면_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";

        given(googleOAuthProvider.getUserInfo(code))
                .willReturn(new OAuthUserInfoResponse(email, name));

        given(jwtTokenProvider.createToken(any(Member.class)))
                .willReturn(accessToken);

        // when
        sut.login(code);

        // then
        assertThat(memberRepository.findByEmail(email)).isPresent();
    }

    @Test
    void 기존회원이면_저장하지_않는다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";

        given(googleOAuthProvider.getUserInfo(code))
                .willReturn(new OAuthUserInfoResponse(email, name));

        given(jwtTokenProvider.createToken(any(Member.class)))
                .willReturn(accessToken);

        Member member = Member.create(name, email);
        memberRepository.save(member);

        // when
        sut.login(code);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 조직에_회원을_성공적으로_등록한다() {
        // given
        var member = Member.create("테스트멤버", "test@test.com");
        memberRepository.save(member);

        var organization = Organization.create(OrganizationService.WOOWACOURSE_NAME, "우아한테크코스 설명", "http://image.url");
        organizationRepository.save(organization);

        // when
        sut.addMemberToWoowacourse(member);

        // then
        var foundOrganization = organizationRepository.findByName(OrganizationService.WOOWACOURSE_NAME).orElseThrow();
        var foundOrganizationMember =
                organizationMemberRepository.findByOrganizationIdAndMemberId(foundOrganization.getId(), member.getId())
                        .orElseThrow();

        assertSoftly(softly -> {
            softly.assertThat(foundOrganizationMember).isNotNull();
            softly.assertThat(foundOrganizationMember.getMember()).isEqualTo(member);
            softly.assertThat(foundOrganizationMember.getOrganization()).isEqualTo(foundOrganization);
            softly.assertThat(foundOrganization.getOrganizationMembers()).contains(foundOrganizationMember);
        });
    }

    @Test
    void 이미_등록된_회원이면_조직에_다시_등록하지_않는다() {
        // given
        var member = Member.create("테스트멤버", "test@test.com");
        memberRepository.save(member);

        // 최초 등록
        sut.addMemberToWoowacourse(member);

        // when
        sut.addMemberToWoowacourse(member);

        // then
        var foundOrganization =
                organizationRepository.findByName(OrganizationService.WOOWACOURSE_NAME).orElseThrow();
        assertThat(foundOrganization.getOrganizationMembers()).hasSize(1);
    }
}
