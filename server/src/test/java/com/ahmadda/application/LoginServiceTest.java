package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.login.RefreshTokenRepository;
import com.ahmadda.infra.login.TokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;

    @MockitoBean
    private GoogleOAuthProvider googleOAuthProvider;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void 신규회원이면_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";
        var refreshToken = "refresh_token";
        var userAgent = createUserAgent();
        var memberToken = new MemberToken(accessToken, refreshToken);

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(tokenProvider.createMemberToken(any(Long.class), any(String.class)))
                .willReturn(memberToken);

        // when
        sut.login(code, redirectUri, userAgent);

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
        var refreshToken = "refresh_token";
        var userAgent = createUserAgent();
        var memberToken = new MemberToken(accessToken, refreshToken);

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(tokenProvider.createMemberToken(any(Long.class), any(String.class)))
                .willReturn(memberToken);

        Member member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 존재하지_않는_회원을_로그아웃_할_수_없다() {
        // given: 회원 하나 만들어두고
        var name = "홍길동";
        var email = "test@example.com";
        var picture = "pic";
        memberRepository.save(Member.create(name, email, picture));

        var userAgent = createUserAgent();
        var refresh = "raw_refresh_token";
        var invalidLoginMember = new LoginMember(999L);

        // when // then
        Assertions.assertThatThrownBy(() -> sut.logout(invalidLoginMember, refresh, userAgent))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    private String createUserAgent() {
        return UUID.randomUUID()
                .toString();
    }
}
