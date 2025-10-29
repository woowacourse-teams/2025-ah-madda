package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.common.exception.NotFoundException;
import com.ahmadda.common.exception.UnauthorizedException;
import com.ahmadda.domain.member.Member;
import com.ahmadda.domain.member.MemberRepository;
import com.ahmadda.domain.member.OpenProfile;
import com.ahmadda.domain.member.OpenProfileRepository;
import com.ahmadda.domain.organization.OrganizationGroup;
import com.ahmadda.domain.organization.OrganizationGroupRepository;
import com.ahmadda.infra.auth.HashEncoder;
import com.ahmadda.infra.auth.RefreshToken;
import com.ahmadda.infra.auth.RefreshTokenRepository;
import com.ahmadda.infra.auth.jwt.JwtProvider;
import com.ahmadda.infra.auth.jwt.config.JwtAccessTokenProperties;
import com.ahmadda.infra.auth.jwt.config.JwtRefreshTokenProperties;
import com.ahmadda.infra.auth.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.auth.oauth.dto.OAuthUserInfoResponse;
import com.ahmadda.infra.notification.slack.SlackAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@EnableConfigurationProperties({JwtAccessTokenProperties.class, JwtRefreshTokenProperties.class})
@RequiredArgsConstructor
public class LoginService {

    private static final String DEFAULT_GROUP_NAME = "기타";

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtProvider jwtProvider;
    private final JwtAccessTokenProperties jwtAccessTokenProperties;
    private final JwtRefreshTokenProperties jwtRefreshTokenProperties;
    private final SlackAlarm slackAlarm;
    private final HashEncoder hashEncoder;
    private final OpenProfileRepository openProfileRepository;
    private final OrganizationGroupRepository organizationGroupRepository;

    @Transactional
    public MemberToken login(final String code, final String redirectUri, final String userAgent) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code, redirectUri);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email(), userInfo.picture());
        MemberToken memberToken = createMemberToken(member.getId());

        rotateRefreshToken(memberToken.refreshToken(), member.getId(), userAgent);

        return memberToken;
    }

    @Transactional
    public MemberToken renewMemberToken(final String refreshToken, final String userAgent) {
        Long memberId = parseRefreshTokenMemberId(refreshToken);
        RefreshToken savedRefreshToken = getRefreshToken(memberId, userAgent);
        validateSavedRefreshTokenMatch(refreshToken, savedRefreshToken);

        MemberToken refreshedMemberToken = createMemberToken(memberId);
        rotateRefreshToken(refreshedMemberToken.refreshToken(), memberId, userAgent);

        return refreshedMemberToken;
    }

    @Transactional
    public void logout(final LoginMember loginMember, final String refreshToken, final String userAgent) {
        Member member = getMember(loginMember);
        RefreshToken savedRefreshToken = getRefreshToken(member.getId(), userAgent);
        validateSavedRefreshTokenMatch(refreshToken, savedRefreshToken);

        refreshTokenRepository.delete(savedRefreshToken);
    }

    private void validateSavedRefreshTokenMatch(final String refreshToken, final RefreshToken savedRefreshToken) {
        String encodedRefreshToken = hashEncoder.encodeSha256(refreshToken);

        if (!encodedRefreshToken.equals(savedRefreshToken.getToken())) {
            throw new UnauthorizedException("리프레시 토큰이 유효하지 않습니다.");
        }
    }

    private Member findOrCreateMember(final String name, final String email, final String profileImageUrl) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email, profileImageUrl);
                    Member savedMember = memberRepository.save(newMember);

                    createOpenProfile(savedMember);

                    slackAlarm.alarmMemberCreation(MemberCreateAlarmPayload.from(newMember));

                    return savedMember;
                });
    }

    private void createOpenProfile(final Member member) {
        OrganizationGroup defaultGroup = getDefaultOrganizationGroup();

        String memberName = member.getName();
        String openProfileName =
                memberName.substring(0, Math.min(memberName.length(), OpenProfile.MAX_NICKNAME_LENGTH));

        OpenProfile openProfile = OpenProfile.create(member, openProfileName, defaultGroup);

        openProfileRepository.save(openProfile);
    }

    private OrganizationGroup getDefaultOrganizationGroup() {
        return organizationGroupRepository.findByName(DEFAULT_GROUP_NAME)
                .orElseGet(() -> {
                    OrganizationGroup defaultGroup = OrganizationGroup.create(DEFAULT_GROUP_NAME);

                    return organizationGroupRepository.save(defaultGroup);
                });
    }

    private Member getMember(final LoginMember loginMember) {
        return memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private RefreshToken getRefreshToken(final Long memberId, final String userAgent) {
        String deviceId = hashEncoder.encodeSha256(userAgent);

        return refreshTokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리프레시 토큰입니다."));
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

    private RefreshToken issueEncodedRefreshToken(
            final String refreshToken,
            final Long memberId,
            final String userAgent
    ) {
        LocalDateTime expiresAt = parseRefreshTokenExpiresAt(refreshToken);

        String encodedToken = hashEncoder.encodeSha256(refreshToken);
        String deviceId = hashEncoder.encodeSha256(userAgent);

        return RefreshToken.create(encodedToken, memberId, deviceId, expiresAt);
    }

    private MemberToken createMemberToken(final Long memberId) {
        String accessToken = jwtProvider.createToken(
                memberId,
                jwtAccessTokenProperties.getAccessExpiration(),
                jwtAccessTokenProperties.getAccessSecretKey()
        );
        String refreshToken = jwtProvider.createToken(
                memberId,
                jwtRefreshTokenProperties.getRefreshExpiration(),
                jwtRefreshTokenProperties.getRefreshSecretKey()
        );

        return new MemberToken(accessToken, refreshToken);
    }

    private LocalDateTime parseRefreshTokenExpiresAt(final String refreshToken) {
        return jwtProvider.parsePayload(refreshToken, jwtRefreshTokenProperties.getRefreshSecretKey())
                .expiresAt();
    }

    private Long parseRefreshTokenMemberId(final String refreshToken) {
        return jwtProvider.parsePayload(refreshToken, jwtRefreshTokenProperties.getRefreshSecretKey())
                .memberId();
    }
}
