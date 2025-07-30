package com.ahmadda.application;

import com.ahmadda.application.dto.MemberCreateAlarmDto;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.jwt.JwtTokenProvider;
import com.ahmadda.infra.oauth.GoogleOAuthProvider;
import com.ahmadda.infra.oauth.dto.OAuthUserInfoResponse;
import com.ahmadda.infra.slack.SlackReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final SlackReminder slackReminder;

    @Transactional
    public String login(final String code, final String redirectUri) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code, redirectUri);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email());
        return jwtTokenProvider.createToken(member.getId());
    }

    private Member findOrCreateMember(final String name, final String email) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);

                    slackReminder.alarmMemberCreation(MemberCreateAlarmDto.from(newMember));

                    return memberRepository.save(newMember);
                });
    }
}
