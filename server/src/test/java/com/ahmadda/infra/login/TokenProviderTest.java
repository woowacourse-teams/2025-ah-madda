package com.ahmadda.infra.login;

import com.ahmadda.infra.login.exception.InvalidTokenException;
import com.ahmadda.infra.login.jwt.JwtProvider;
import com.ahmadda.infra.login.jwt.dto.JwtMemberPayload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class TokenProviderTest {

    @Autowired
    TokenProvider sut;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    JwtProvider jwtProvider;

    @Test
    void 액세스_리프레시_토큰_생성시_리프레시_해시를_저장한다() {
        // given
        var memberId = 1L;
        when(jwtProvider.createAccessToken(memberId)).thenReturn("AT_1");
        when(jwtProvider.createRefreshToken(memberId)).thenReturn("RT_1");

        // when
        var memberToken = sut.createMemberToken(memberId);

        // then
        assertSoftly(softly -> {
            softly.assertThat(memberToken.accessToken())
                    .isEqualTo("AT_1");
            softly.assertThat(memberToken.refreshToken())
                    .isEqualTo("RT_1");
        });
    }

    @Test
    void 만료된_액세스_리프레시_일치시_재발급_및_로테이션이_된다() {
        // given
        var memberId = 1L;

        var oldRawRT = "RT_OLD";
        var oldHash = passwordEncoder.encode(oldRawRT);
        refreshTokenRepository.save(RefreshToken.create(oldHash, memberId));

        when(jwtProvider.isAccessTokenExpired("AT_EXPIRED")).thenReturn(true);

        var payload = Mockito.mock(JwtMemberPayload.class);
        when(payload.getMemberId()).thenReturn(memberId);
        when(jwtProvider.parseRefreshPayload(oldRawRT)).thenReturn(payload);

        when(jwtProvider.createAccessToken(memberId)).thenReturn("AT_NEW");
        when(jwtProvider.createRefreshToken(memberId)).thenReturn("RT_NEW");

        // when
        var memberToken = sut.renewMemberToken("AT_EXPIRED", oldRawRT);

        // then
        assertSoftly(softly -> {
            softly.assertThat(memberToken.accessToken())
                    .isEqualTo("AT_NEW");
            softly.assertThat(memberToken.refreshToken())
                    .isEqualTo("RT_NEW");
        });
    }

    @Test
    void 액세스가_아직_만료되지_않으면_재발급시_예외가_발생한다() {
        // given
        when(jwtProvider.isAccessTokenExpired("AT_VALID")).thenReturn(false);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken("AT_VALID", "ANY_RT"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("아직 만료되지 않은 액세스 토큰");
    }

    @Test
    void 리프레시_토큰_해시가_일치하지_않으면_예외가_발생한다() {
        // given
        var memberId = 1L;
        var savedRawRT = "RT_SAVED";
        refreshTokenRepository.save(RefreshToken.create(passwordEncoder.encode(savedRawRT), memberId));

        when(jwtProvider.isAccessTokenExpired("AT_EXPIRED")).thenReturn(true);

        var payload = Mockito.mock(JwtMemberPayload.class);
        when(payload.getMemberId()).thenReturn(memberId);
        when(jwtProvider.parseRefreshPayload("RT_OTHER")).thenReturn(payload);

        // when // then
        assertThatThrownBy(() -> sut.renewMemberToken("AT_EXPIRED", "RT_OTHER"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("리프레시 토큰이 일치하지 않습니다");
    }

    @Test
    void 특정_멤버의_리프레시토큰을_삭제할_수_있다() {
        // given
        var memberId = 1L;
        refreshTokenRepository.save(RefreshToken.create(passwordEncoder.encode("RT_X"), memberId));

        // when
        sut.deleteRefreshToken(memberId);

        // then
        assertThat(refreshTokenRepository.findByMemberId(memberId)).isEmpty();
    }

    @Test
    void 존재하지_않는_멤버의_RT_삭제시_예외가_발생한다() {
        // given
        var memberId = 1L;

        // when // then
        assertThatThrownBy(() -> sut.deleteRefreshToken(memberId))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("토큰을 찾을 수 없습니다");
    }
}
