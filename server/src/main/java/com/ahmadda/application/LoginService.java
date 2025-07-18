package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.GoogleOAuthProvider;
import com.ahmadda.infra.JwtTokenProvider;
import com.ahmadda.infra.OAuthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String login(final String code) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code);

        Member member = findOrCreateMember(userInfo.email(), userInfo.name());

        return jwtTokenProvider.createAccessToken(member);
    }

    private Member findOrCreateMember(final String email, final String name) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);

                    return memberRepository.save(newMember);
                });
    }
}
