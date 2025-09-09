package com.ahmadda.application;

import com.ahmadda.annotation.IntegrationTest;
import com.ahmadda.application.dto.FcmRegistrationTokenRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@IntegrationTest
class FcmRegistrationTokenServiceTest {

    @Autowired
    private FcmRegistrationTokenService sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @MockitoBean
    JwtAccessTokenProperties accessTokenProperties;

    @MockitoBean
    JwtRefreshTokenProperties refreshTokenProperties;

    @Test
    void 푸시알림_수신자를_등록한다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "test@example.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());
        var request = new FcmRegistrationTokenRequest("토큰값");

        // when
        var saved = sut.registerFcmRegistrationToken(request, loginMember);

        // then
        assertThat(fcmRegistrationTokenRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(fcmRegistrationToken -> {
                    assertSoftly(softly -> {
                        softly.assertThat(fcmRegistrationToken.getMemberId())
                                .isEqualTo(member.getId());
                        softly.assertThat(fcmRegistrationToken.getRegistrationToken())
                                .isEqualTo("토큰값");
                    });
                });
    }

    @Test
    void 중복된_토큰을_등록하면_기존_토큰을_반환하고_새로_저장하지_않는다() {
        // given
        var member = memberRepository.save(Member.create("홍길동", "test@example.com", "testPicture"));
        var loginMember = new LoginMember(member.getId());
        var request = new FcmRegistrationTokenRequest("중복된토큰");

        var firstSaved = sut.registerFcmRegistrationToken(request, loginMember);

        // when
        var secondSaved = sut.registerFcmRegistrationToken(request, loginMember);

        // then
        assertSoftly(softly -> {
            softly.assertThat(secondSaved.getId())
                    .isEqualTo(firstSaved.getId());
            softly.assertThat(fcmRegistrationTokenRepository.count())
                    .isEqualTo(1);
        });
    }

    @Test
    void 회원이_존재하지_않으면_예외가_발생한다() {
        // given
        var nonExistentMemberId = 999L;
        var loginMember = new LoginMember(nonExistentMemberId);
        var request = new FcmRegistrationTokenRequest("임의의토큰");

        // when // then
        assertThatThrownBy(() -> sut.registerFcmRegistrationToken(request, loginMember))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}
