package com.ahmadda.application;

import com.ahmadda.application.exception.NotFoundException;
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

    public String publishLoginToken(final Member member) {
        if (!memberRepository.existsMemberByEmail(member.getEmail())) {
            throw new NotFoundException("해당 사용자를 찾을 수 없음 email : " + member.getEmail());
        }

        return jwtTokenProvider.createToken(member.getName(), member.getEmail());
    }
}
