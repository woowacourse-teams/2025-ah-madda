package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.GoogleOAuthProvider;
import com.ahmadda.infra.JwtTokenProvider;
import com.ahmadda.infra.OAuthUserInfoResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
@Import(LoginServiceTest.TestBeans.class)
class LoginServiceTest {

    @Autowired
    private LoginService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GoogleOAuthProvider googleOAuthProvider;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void login_신규회원이면_DB에_저장된다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var accessToken = "access_token";

        given(googleOAuthProvider.getUserInfo(code))
                .willReturn(new OAuthUserInfoResponse(email, name));

        given(jwtTokenProvider.createAccessToken(any(Member.class)))
                .willReturn(accessToken);

        // when
        sut.login(code);

        // then
        assertThat(memberRepository.findByEmail(email)).isPresent();
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        public GoogleOAuthProvider googleOAuthProvider() {
            return Mockito.mock(GoogleOAuthProvider.class);
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }
    }
}
