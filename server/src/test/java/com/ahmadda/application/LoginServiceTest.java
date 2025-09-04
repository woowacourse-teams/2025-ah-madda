package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.OrganizationMemberRepository;
import com.ahmadda.domain.organization.OrganizationRepository;
import com.ahmadda.infra.auth.RefreshTokenRepository;
import com.ahmadda.infra.auth.exception.InvalidTokenException;
import com.ahmadda.infra.auth.jwt.config.JwtProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.auth.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.auth.oauth.dto.OAuthUserInfoResponse;
import com.ahmadda.infra.auth.util.HashUtils;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@IntegrationTest
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

    @Autowired
    private JwtProperties jwtProperties;

    @MockitoBean
    private GoogleOAuthProvider googleOAuthProvider;

    @Test
    void 신규회원이면_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

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
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 로그인을하여_엑세스토큰과_리프레시_토큰을_저장한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        assertThat(refreshTokenRepository.findAll()).hasSize(1);
    }

    @Test
    void 로그인_시_이전의_토큰을_제거한다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        sut.login(code, redirectUri, userAgent);

        var deviceId = HashUtils.sha256(userAgent);
        var oldSavedToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                .get();

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        assertSoftly(softly -> {
            var newSavedToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                    .get();
            softly.assertThat(oldSavedToken.getId())
                    .isNotEqualTo(newSavedToken.getId());
            softly.assertThat(refreshTokenRepository.findAll())
                    .hasSize(1);
        });
    }

    @Test
    void 액세스토큰이_만료된_경우_리프레시_토큰을_재발급_받을_수_있다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";
        var deviceId = HashUtils.sha256(userAgent);

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        sut.login(code, redirectUri, userAgent);
        var expiredAccessToken = createExpiredAccessToken(member.getId());

        var loginTokens = sut.login(code, redirectUri, userAgent);

        var oldSavedToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                .get();

        // when
        sut.renewMemberToken(expiredAccessToken, loginTokens.refreshToken(), userAgent);

        // then
        assertSoftly(softly -> {
            var newSavedToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                    .get();
            softly.assertThat(oldSavedToken.getId())
                    .isNotEqualTo(newSavedToken.getId());
            softly.assertThat(refreshTokenRepository.findAll())
                    .hasSize(1);
        });
    }

    @Test
    void 액세스토큰이_만료되지_않은_경우_재발급_받을_수_없다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        var loginTokens = sut.login(code, redirectUri, userAgent);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(
                loginTokens.accessToken(),
                loginTokens.refreshToken(),
                userAgent
        ))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("엑세스 토큰이 만료되지 않았습니다.");
    }

    @Test
    void 존재하지_않는_회원을_로그아웃_하면_예외가_발생한다() {
        // given
        var name = "홍길동";
        var email = "test@example.com";
        var picture = "pic";
        memberRepository.save(Member.create(name, email, picture));

        var userAgent = createUserAgent();
        var refresh = "raw_refresh_token";
        var invalidLoginMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.logout(invalidLoginMember, refresh, userAgent))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다");
    }

    @Test
    void 로그아웃을_정상적으로_할_수_있다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = Member.create(name, email, testPicture);
        memberRepository.save(member);
        var loginMember = new LoginMember(member.getId());

        var newLoginToken = sut.login(code, redirectUri, userAgent);

        // when
        sut.logout(loginMember, newLoginToken.refreshToken(), userAgent);

        // then
        assertThat(refreshTokenRepository.findAll())
                .hasSize(0);
    }

    private String createUserAgent() {
        return UUID.randomUUID()
                .toString();
    }

    private String createExpiredAccessToken(Long memberId) {
        var now = Instant.now();

        var claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.minus(Duration.ofDays(1))))
                .signWith(jwtProperties.getAccessSecretKey())
                .compact();
    }
}
