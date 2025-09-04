package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.infra.alarm.slack.SlackAlarm;
import com.ahmadda.infra.login.RefreshToken;
import com.ahmadda.infra.login.RefreshTokenRepository;
import com.ahmadda.infra.login.TokenProvider;
import com.ahmadda.infra.login.util.HashUtils;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final TokenProvider tokenProvider;
    private final SlackAlarm slackAlarm;

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
        Long memberId = tokenProvider.parseRefreshTokenMemberId(refreshToken);

        RefreshToken savedRefreshToken = getRefreshToken(memberId, userAgent);
        MemberToken refreshedMemberToken =
                tokenProvider.refreshMemberToken(accessToken, refreshToken, savedRefreshToken.getToken());

        rotateRefreshToken(refreshedMemberToken.refreshToken(), memberId, userAgent);

        return refreshedMemberToken;
    }

    @Transactional
    public void logout(final LoginMember loginMember, final String refreshToken, final String userAgent) {
        Member member = getMember(loginMember);
        RefreshToken savedRefreshToken = getRefreshToken(member.getId(), userAgent);

        tokenProvider.validateRefreshTokenMatch(refreshToken, savedRefreshToken.getToken(), member.getId());

        refreshTokenRepository.delete(savedRefreshToken);
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
        String deviceId = HashUtils.sha256(userAgent);

        return refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)
                .orElseThrow(() -> new NotFoundException("토큰을 찾을 수 없습니다."));
    }

    private void rotateRefreshToken(final String refreshToken, final Long memberId, final String userAgent) {
        deleteExistRefreshToken(memberId, userAgent);

        saveRefreshToken(refreshToken, memberId, userAgent);
    }

    private void deleteExistRefreshToken(final Long memberId, final String userAgent) {
        String deviceId = HashUtils.sha256(userAgent);

        refreshTokenRepository.deleteByMemberIdAndDeviceId(memberId, deviceId);
    }

    private void saveRefreshToken(final String refreshToken, final Long memberId, final String userAgent) {
        LocalDateTime expiresAt = tokenProvider.parseRefreshTokenExpiresAt(refreshToken);

        RefreshToken createdRefreshToken = RefreshToken.create(refreshToken, memberId, userAgent, expiresAt);

        refreshTokenRepository.save(createdRefreshToken);
    }
}
