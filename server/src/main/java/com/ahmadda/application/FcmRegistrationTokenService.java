package com.ahmadda.application;

import com.ahmadda.application.dto.FcmRegistrationTokenRequest;
import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.notification.push.FcmRegistrationToken;
import com.ahmadda.infra.notification.push.FcmRegistrationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmRegistrationTokenService {

    private final MemberRepository memberRepository;
    private final FcmRegistrationTokenRepository fcmRegistrationTokenRepository;

    @Transactional
    public FcmRegistrationToken registerFcmRegistrationToken(
            final FcmRegistrationTokenRequest request,
            final LoginMember loginMember
    ) {
        return fcmRegistrationTokenRepository.findByRegistrationTokenAndMemberId(
                        request.registrationToken(),
                        loginMember.memberId()
                )
                .orElseGet(() -> {
                    Member member = memberRepository.findById(loginMember.memberId())
                            .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

                    FcmRegistrationToken registrationToken = FcmRegistrationToken.createNow(
                            member.getId(),
                            request.registrationToken()
                    );

                    return fcmRegistrationTokenRepository.save(registrationToken);
                });
    }
}
