package com.ahmadda.application;

import com.ahmadda.application.dto.LoginMember;
import com.ahmadda.application.dto.PushNotificationRecipientRequest;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.Member;
import com.ahmadda.domain.MemberRepository;
import com.ahmadda.domain.PushNotificationRecipient;
import com.ahmadda.domain.PushNotificationRecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushNotificationRecipientService {

    private final MemberRepository memberRepository;
    private final PushNotificationRecipientRepository pushNotificationRecipientRepository;

    @Transactional
    public PushNotificationRecipient registerRecipient(
            final PushNotificationRecipientRequest request,
            final LoginMember loginMember
    ) {
        Member member = memberRepository.findById(loginMember.memberId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        PushNotificationRecipient pushNotificationRecipient = PushNotificationRecipient.create(
                member,
                request.notificationToken()
        );

        return pushNotificationRecipientRepository.save(pushNotificationRecipient);
    }
}
