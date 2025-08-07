package com.ahmadda.application;

import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.OrganizationMemberRepository;
import com.ahmadda.domain.OrganizationRepository;
import com.ahmadda.infra.jwt.JwtProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private JwtProvider jwtProvider;

    @Test
    void 신규회원이면_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";
        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(jwtProvider.createToken(any(Long.class)))
                .willReturn(accessToken);

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
        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(jwtProvider.createToken(any(Long.class)))
                .willReturn(accessToken);

        Member member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        // when
        sut.login(code, redirectUri);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 로그인시_회원을_저장하고_알람_요청을_보낸다(CapturedOutput output) {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";
        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        var memberCreateAlarmDto = new MemberCreateAlarmPayload(name, email);
        var expectedLog = String.format(
                "회원가입 유저 정보 : %s 프로덕션이 아니어서 슬랙으로 알람 보내지 않음",
                memberCreateAlarmDto
        );

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        given(jwtProvider.createToken(any(Long.class)))
                .willReturn(accessToken);

        // when
        sut.login(code, redirectUri);

        // then
        assertThat(output.getOut()).contains(expectedLog);
    }

}
