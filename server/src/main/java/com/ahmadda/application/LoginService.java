package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.common.exception.InvalidTokenException;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.infra.auth.HashEncoder;
import com.ahmadda.infra.auth.RefreshToken;
import com.ahmadda.infra.auth.RefreshTokenRepository;
import com.ahmadda.infra.auth.TokenProvider;
import com.ahmadda.infra.auth.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.auth.oauth.dto.OAuthUserInfoResponse;
import com.ahmadda.infra.notification.slack.SlackAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final TokenProvider tokenProvider;
    private final SlackAlarm slackAlarm;
    private final HashEncoder hashEncoder;

    @Transactional
    public MemberToken login(final String code, final String redirectUri, final String userAgent) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code, redirectUri);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email(), userInfo.picture());
        MemberToken memberToken = tokenProvider.createMemberToken(member.getId());

        rotateRefreshToken(memberToken.refreshToken(), member.getId(), userAgent);

        return memberToken;
    }

    @Transactional
    public MemberToken renewMemberToken(final String accessToken, final String refreshToken, final String userAgent) {
        validateTokenExpired(accessToken, refreshToken);

        Long memberId = tokenProvider.parseRefreshTokenMemberId(refreshToken);
        RefreshToken savedRefreshToken = getRefreshToken(memberId, userAgent);

        validateTokenMatch(refreshToken, savedRefreshToken, memberId);

        MemberToken refreshedMemberToken =
                tokenProvider.createMemberToken(memberId);

        rotateRefreshToken(refreshedMemberToken.refreshToken(), memberId, userAgent);

        return refreshedMemberToken;
    }

    @Transactional
    public void logout(final LoginMember loginMember, final String refreshToken, final String userAgent) {
        Member member = getMember(loginMember);
        RefreshToken savedRefreshToken = getRefreshToken(member.getId(), userAgent);

        validateTokenMatch(refreshToken, savedRefreshToken, member.getId());

        refreshTokenRepository.delete(savedRefreshToken);
    }

    private void validateTokenExpired(final String accessToken, final String refreshToken) {
        validateAccessTokenNotActive(accessToken);
        validateRefreshTokenActive(refreshToken);
    }

    private void validateTokenMatch(final String refreshToken,
                                    final RefreshToken savedRefreshToken,
                                    final Long memberId) {
        validateRefreshTokenMatch(refreshToken, memberId);
        validateSavedRefreshTokenMatch(refreshToken, savedRefreshToken);
    }

    private void validateRefreshTokenMatch(final String refreshToken,
                                           final Long memberId) {
        Long refreshTokenMemberId = tokenProvider.parseRefreshTokenMemberId(refreshToken);
        if (!Objects.equals(memberId, refreshTokenMemberId)) {
            throw new InvalidTokenException("토큰 정보가 일치하지 않습니다.");
        }
    }

    private void validateSavedRefreshTokenMatch(final String refreshToken, final RefreshToken savedRefreshToken) {
        String encodedRefreshToken = hashEncoder.encodeSha256(refreshToken);

        if (!encodedRefreshToken.equals(savedRefreshToken.getToken())) {
            throw new InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }
    }

    private void validateAccessTokenNotActive(final String accessToken) {
        Optional<Boolean> accessTokenExpired = tokenProvider.checkAccessTokenExpired(accessToken);

        if (accessTokenExpired.isEmpty()) {
            throw new InvalidTokenException("엑세스 토큰이 올바르지 않습니다.");
        }

        if (Objects.equals(accessTokenExpired.get(), Boolean.FALSE)) {
            throw new InvalidTokenException("엑세스 토큰이 만료되지 않았습니다.");
        }
    }

    private void validateRefreshTokenActive(final String refreshToken) {
        Optional<Boolean> refreshTokenExpired = tokenProvider.checkRefreshTokenExpired(refreshToken);

        if (refreshTokenExpired.isEmpty()) {
            throw new InvalidTokenException("리프레시 토큰이 올바르지 않습니다.");
        }

        if (Objects.equals(refreshTokenExpired.get(), Boolean.TRUE)) {
            throw new InvalidTokenException("리프레시 토큰이 만료되었습니다.");
        }
    }

    private Member findOrCreateMember(final String name, final String email, final String profileImageUrl) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email, profileImageUrl);

                    slackAlarm.alarmMemberCreation(MemberCreateAlarmPayload.from(newMember));

                    return memberRepository.save(newMember);
                });
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다"));
    }

    private RefreshToken getRefreshToken(final Long memberId, final String userAgent) {
        String deviceId = hashEncoder.encodeSha256(userAgent);

        return refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)
                .orElseThrow(() -> new NotFoundException("토큰을 찾을 수 없습니다."));
    }

    private void rotateRefreshToken(final String refreshToken, final Long memberId, final String userAgent) {
        deleteExistRefreshToken(memberId, userAgent);

        RefreshToken encodedRefreshToken = issueEncodedRefreshToken(refreshToken, memberId, userAgent);

        refreshTokenRepository.save(encodedRefreshToken);
    }

    private void deleteExistRefreshToken(final Long memberId, final String userAgent) {
        String deviceId = hashEncoder.encodeSha256(userAgent);

        refreshTokenRepository.deleteByMemberIdAndDeviceId(memberId, deviceId);
    }

    private RefreshToken issueEncodedRefreshToken(final String refreshToken,
                                                  final Long memberId,
                                                  final String userAgent) {
        LocalDateTime expiresAt = tokenProvider.parseRefreshTokenExpiresAt(refreshToken);

        String encodedToken = hashEncoder.encodeSha256(refreshToken);
        String deviceId = hashEncoder.encodeSha256(userAgent);

        return RefreshToken.create(encodedToken, memberId, deviceId, expiresAt);
    }
}
