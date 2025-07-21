package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.jwt.JwtTokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class LoginServiceTest {

    @Autowired
    private LoginService sut;

    @Autowired
    private MemberRepository memberRepository;

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
}
