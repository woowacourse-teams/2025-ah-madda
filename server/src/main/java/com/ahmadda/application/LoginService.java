package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
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

    public String renewAuthTokens(final String refreshToken) {
        long memberId = jwtTokenProvider.extractId(refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("member를 찾을 수 없습니다."));
        return jwtTokenProvider.createAccessToken(member);
    }
}
