package com.ahmadda.application;

import com.ahmadda.application.config.TokenPolicyProperties;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.GoogleOAuthProvider;
import com.ahmadda.infra.JwtTokenProvider;
import com.ahmadda.infra.dto.OAuthUserInfoResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenPolicyProperties.class)
public class LoginService {

    private final MemberRepository memberRepository;
    private final GoogleOAuthProvider googleOAuthProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPolicyProperties tokenPolicyProperties;

    @Transactional
    public String login(final String code) {
        OAuthUserInfoResponse userInfo = googleOAuthProvider.getUserInfo(code);

        Member member = findOrCreateMember(userInfo.name(), userInfo.email());
        Claims accessTokenClaims = createAccessTokenClaims(member);

        return jwtTokenProvider.createToken(accessTokenClaims, tokenPolicyProperties.getAccessExpiration());
    }

    private Member findOrCreateMember(final String name, final String email) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    Member newMember = Member.create(name, email);

                    return memberRepository.save(newMember);
                });
    }

    private Claims createAccessTokenClaims(final Member member) {
        return Jwts.claims()
                .subject(member.getId().toString())
                .add("name", member.getName())
                .add("email", member.getEmail())
                .build();
    }
}
