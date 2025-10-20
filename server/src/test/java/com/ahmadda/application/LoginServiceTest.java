package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnauthorizedException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.infra.auth.HashEncoder;
import com.ahmadda.infra.auth.RefreshTokenRepository;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import com.ahmadda.infra.auth.jwt.dto.JwtMemberPayload;
import com.ahmadda.infra.auth.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.auth.oauth.dto.OAuthUserInfoResponse;
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
    private JwtRefreshTokenProperties refreshTokenProperties;

    @Autowired
    private HashEncoder hashEncoder;

    @Autowired
    private OrganizationGroupRepository organizationGroupRepository;

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
        var testPicture = "testPicture";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        createMember();

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @Test
    void 로그인을하면_리프레시토큰이_저장된다() {
        // given
        var code = "code";
        var email = "test@example.com";
        var redirectUri = "redirectUri";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, "홍길동", "pic"));

        // when
        sut.login(code, redirectUri, createUserAgent());

        // then
        assertThat(refreshTokenRepository.findAll()).hasSize(1);
    }

    @Test
    void 로그인_시_기존토큰은_제거된다() {
        // given
        var code = "code";
        var email = "test@example.com";
        var userAgent = createUserAgent();
        var redirectUri = "redirectUri";

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, "홍길동", "pic"));

        var member = createMember();

        sut.login(code, redirectUri, userAgent);

        var deviceId = hashEncoder.encodeSha256(userAgent);
        var oldToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                .get();

        // when
        sut.login(code, redirectUri, userAgent);

        // then
        var newToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                .get();
        assertSoftly(softly -> {
            softly.assertThat(oldToken.getId())
                    .isNotEqualTo(newToken.getId());
            softly.assertThat(refreshTokenRepository.findAll())
                    .hasSize(1);
        });
    }

    @Test
    void 리프레시_토큰을_재발급_받을_수_있다() {
        // given
        var code = "code";
        var name = "홍길동";
        var email = "test@example.com";
        var userAgent = createUserAgent();

        var redirectUri = "redirectUri";
        var testPicture = "testPicture";
        var deviceId = hashEncoder.encodeSha256(userAgent);

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, name, testPicture));

        var member = createMember();

        sut.login(code, redirectUri, userAgent);

        var loginTokens = sut.login(code, redirectUri, userAgent);

        var oldSavedToken = refreshTokenRepository.findByMemberIdAndDeviceId(member.getId(), deviceId)
                .get();

        // when
        sut.renewMemberToken(loginTokens.refreshToken(), userAgent);

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
    void 리프레시토큰이_저장된값과_불일치하면_예외가_발생한다() {
        // given
        var member = createMember();
        var userAgent = createUserAgent();
        var redirectUri = "redirectUri";
        var email = "test@example.com";

        given(googleOAuthProvider.getUserInfo("code", redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, "홍길동", "pic"));

        sut.login("code", redirectUri, userAgent);
        var otherRefresh = createValidRefreshToken(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(otherRefresh, userAgent))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("리프레시 토큰이 유효하지 않습니다.");
    }

    @Test
    void 저장된_리프레시토큰이_없으면_예외가_발생한다() {
        // given
        var member = createMember();
        var userAgent = createUserAgent();

        var refresh = createValidRefreshToken(member.getId());

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken(refresh, userAgent))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 리프레시 토큰입니다.");
    }

    @Test
    void 로그아웃을_정상적으로_수행한다() {
        // given
        var code = "code";
        var email = "test@example.com";
        var redirectUri = "redirectUri";
        var userAgent = createUserAgent();

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, "홍길동", "pic"));

        var member = createMember();
        var loginMember = new LoginMember(member.getId());
        var tokens = sut.login(code, redirectUri, userAgent);

        // when
        sut.logout(loginMember, tokens.refreshToken(), userAgent);

        // then
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    @Test
    void 존재하지않는_회원이_로그아웃하면_예외가_발생한다() {
        // given
        var invalidMember = new LoginMember(999L);

        // when // then
        assertThatThrownBy(() -> sut.logout(invalidMember, "refresh", createUserAgent()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 로그아웃시_리프레시토큰이_불일치하면_예외가_발생한다() {
        // given
        var code = "code";
        var email = "test@example.com";
        var redirectUri = "redirectUri";
        var userAgent = createUserAgent();

        given(googleOAuthProvider.getUserInfo(code, redirectUri))
                .willReturn(new OAuthUserInfoResponse(email, "홍길동", "pic"));

        var member = createMember();
        var loginMember = new LoginMember(member.getId());
        sut.login(code, redirectUri, userAgent);

        // when // then
        assertThatThrownBy(() -> sut.logout(loginMember, "invalidRefreshToken", userAgent))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("리프레시 토큰이 유효하지 않습니다.");
    }

    private String createUserAgent() {
        return UUID.randomUUID()
                .toString();
    }

    private Member createMember() {
        var name = "홍길동";
        var email = "test@example.com";
        var testPicture = "testPicture";

        Member member = Member.create(name, email, testPicture);
        memberRepository.save(member);

        return member;
    }

    private String createValidRefreshToken(Long memberId) {
        var now = Instant.now();
        var claims = JwtMemberPayload.toClaims(memberId);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofDays(1))))
                .signWith(refreshTokenProperties.getRefreshSecretKey())
                .compact();
    }
}
