package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.PushNotificationRecipientRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.infra.notification.push.FcmPushToken;
import com.ahmadda.infra.notification.push.FcmPushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FcmPushTokenService {

    private final MemberRepository memberRepository;
    private final FcmPushTokenRepository fcmPushTokenRepository;

    @Transactional
    public FcmPushToken registerFcmPushToken(
            final PushNotificationRecipientRequest request,
            final LoginMember loginMember
    ) {
        Member member = memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        FcmPushToken fcmPushToken = FcmPushToken.create(
                member.getId(),
                request.fcmPushToken(),
                LocalDateTime.now()
        );

        return fcmPushTokenRepository.save(fcmPushToken);
    }
}
