package com.ahmadda.application;

import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member processGoogleOAuthLogin(GoogleOAuthUserInfo userInfo) {
        return memberRepository.findByEmail(userInfo.email())
                .orElseGet(() -> {
                    Member newMember = Member.create(userInfo.name(), userInfo.email());
                    return memberRepository.save(newMember);
                });
    }
}
