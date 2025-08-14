package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.login.TokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ExtendWith(OutputCaptureExtension.class)
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
    private TokenProvider tokenProvider;

    @Test
    void 신규회원이면_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";
        var refreshToken = "refresh_token";
        var memberToken = new MemberToken(accessToken, refreshToken);

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(tokenProvider.createMemberToken(any(Long.class)))
                .willReturn(memberToken);

        // when
        sut.login(code, redirectUri);

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
        var memberToken = new MemberToken(accessToken, refreshToken);

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(tokenProvider.createMemberToken(any(Long.class)))
                .willReturn(memberToken);

        Member member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        // when
        sut.login(code, redirectUri);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 리프레시_토큰을_재발급_할_수_있다() {
        // given
        String access = "expired_access";
        String refresh = "valid_refresh";
        MemberToken rotated = new MemberToken("new_access", "new_refresh");
        given(tokenProvider.renewMemberToken(access, refresh)).willReturn(rotated);

        // when
        MemberToken result = sut.renewMemberToken(access, refresh);

        // then
        assertThat(result).isSameAs(rotated);
    }

    @Test
    void 존재하는_회원만_로그아웃_할_수_있다() {
        // given
        LoginMember loginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.logout(loginMember))
                .isInstanceOf(NotFoundException.class);
    }
}
