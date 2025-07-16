package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String publishLoginToken(Member member) {
        if(!memberRepository.existsMemberByEmail(member.getEmail())) {
            throw new RuntimeException("");
        }

        return jwtTokenProvider.createToken(member.getName(), member.getEmail());
    }
}
