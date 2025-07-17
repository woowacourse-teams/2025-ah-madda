package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthTokens login(final String code) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code);
        Member member = findOrCreateMember(userInfo.email(), userInfo.name());

        return jwtTokenProvider.publishLoginTokens(member);
    }

    private Member findOrCreateMember(final String email, String name) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);
                    return memberRepository.save(newMember);
                });
    }
}
