package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.MemberCreateAlarmPayload;
import com.ahmadda.application.dto.MemberToken;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.alarm.slack.SlackAlarm;
import com.ahmadda.infra.login.TokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final TokenProvider tokenProvider;
    private final SlackAlarm slackAlarm;

    @Transactional
    public MemberToken login(final String code, final String redirectUri) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code, redirectUri);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email(), userInfo.picture());

        return tokenProvider.getMemberToken(member.getId());
    }

//    public MemberToken renewMemberToken(final String accessToken, final String refreshToken) {
//        tokenProvider.validateToken(refreshToken);
//
//        return tokenProvider.getRenewMemberToken(accessToken, refreshToken);
//    }

    @Transactional
    public void logout(final LoginMember loginMember) {
        Member member = getMember(loginMember);

        tokenProvider.deleteRefreshToken(member.getId());
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
}
